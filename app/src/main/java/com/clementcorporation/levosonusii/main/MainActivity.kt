package com.clementcorporation.levosonusii.main

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.DEFAULT_VOICE_COMMAND_PROMPT
import com.clementcorporation.levosonusii.main.Constants.PROMPT_KEYWORD
import com.clementcorporation.levosonusii.main.Constants.VOICE_COMMAND_KEY
import com.clementcorporation.levosonusii.main.ui.theme.LevoSonusIITheme
import com.clementcorporation.levosonusii.navigation.LevoSonusNavigation
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val TAG = "MainActivity"
@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity(){
    private lateinit var navController: NavHostController
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false
                )) -> {
                getCurrentLocation()
            } else -> {
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        if (resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
            results?.let {
                executeVoiceCommand(it)
            }
        }
    }
    private lateinit var bManager: LocalBroadcastManager
    private val bReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == Constants.USER_INPUT) {
                val userInput = intent.getStringExtra(VOICE_COMMAND_KEY)
                userInput?.let {
                    executeVoiceCommand(it)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val intentFilter = IntentFilter().apply {
                addAction(Constants.USER_INPUT)
            }
            viewModel = hiltViewModel()
            val uiState: MainActivityEvents by viewModel.mainActivityEventsState.collectAsStateWithLifecycle()
            bManager = LocalBroadcastManager.getInstance(this).apply {
                registerReceiver(bReceiver, intentFilter)
            }
            navController = rememberNavController()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
            when(uiState) {
                is MainActivityEvents.OnShowVoiceCommandActivity -> {
                    val i = Intent(this@MainActivity, VoiceCommandActivity::class.java).apply {
                        putExtra(PROMPT_KEYWORD, (uiState as MainActivityEvents.OnShowVoiceCommandActivity).title)
                        putExtra(VoiceCommandActivity.IS_TRAINING_MODE, (uiState as MainActivityEvents.OnShowVoiceCommandActivity).isTrainingMode)
                    }
                    resultLauncher.launch(i)
                }
                is MainActivityEvents.OnFetchUserOrganization -> {
                    if ((uiState as MainActivityEvents.OnFetchUserOrganization).name?.isNotEmpty() == true) {
                        navController.navigate(LevoSonusScreens.LoginScreen.name)
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.organization_name_success_toast_message, (uiState as MainActivityEvents.OnFetchUserOrganization).name),
                            Toast.LENGTH_LONG
                        ).show()
                        startVoiceCommandService()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.organization_name_failed_toast_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else -> {}
            }

            LevoSonusIITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier.padding(8.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                val showFAB = remember {
                                    mutableStateOf(false)
                                }
                                LevoSonusNavigation(navController, showFAB, viewModel::showVoiceCommandActivity)
                                if (showFAB.value) {
                                    LSFAB {
                                        viewModel.showVoiceCommandActivity(DEFAULT_VOICE_COMMAND_PROMPT)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun executeVoiceCommand(command: String) {
        if (!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
            when (command) {
                VoiceCommands.ANNOUNCEMENTS -> {
                    navController.navigate(LevoSonusScreens.AnnouncementsScreen.name)
                }
                VoiceCommands.BENEFITS -> {
                    navController.navigate(LevoSonusScreens.PayAndBenefitsScreen.name)
                }
                VoiceCommands.DEPARTMENTS-> {
                    navController.navigate(LevoSonusScreens.DepartmentsScreen.name)
                }
                VoiceCommands.EQUIPMENT -> {
                    navController.navigate(LevoSonusScreens.EquipmentScreen.name)
                }
                VoiceCommands.GAME_CENTER -> {
                    navController.navigate(LevoSonusScreens.GameCenterScreen.name)
                }
                VoiceCommands.HOME -> {
                    navController.navigate(LevoSonusScreens.HomeScreen.name)
                }
                VoiceCommands.HEALTH -> {
                    navController.navigate(LevoSonusScreens.HealthAndWellnessScreen.name)
                }
                VoiceCommands.MESSAGES -> {
                    navController.navigate(LevoSonusScreens.MessengerScreen.name)
                }
                VoiceCommands.ORDERS -> {
                    navController.navigate(LevoSonusScreens.OrdersScreen.name)
                }
                VoiceCommands.SIGN_OUT -> {
                    viewModel.signOut()
                    navController.navigate(LevoSonusScreens.LoginScreen.name)
                }
                VoiceCommands.VOICE_PROFILE -> {
                    navController.navigate(LevoSonusScreens.VoiceProfileScreen.name)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopService()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
        bManager.unregisterReceiver(bReceiver)
    }

    private fun stopService() {
        try {
            stopService(Intent(this, LevoSonusService::class.java))
        } catch(e: Exception) {
            e.localizedMessage?.let { Log.d(TAG, it) }
        }
    }

    private fun getCurrentLocation() {
        var address = ""
        when (
            ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            PackageManager.PERMISSION_GRANTED -> {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                        var latitude = 0.0
                        var longitude = 0.0
                        location?.latitude?.let { latitude = it }
                        location?.longitude?.let { longitude = it }
                        val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                        if (Build.VERSION.SDK_INT >= 33) {
                            geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                                address = addresses.first().getAddressLine(0).toString()
                            }
                        } else {
                            val addressFromGeocoder =
                                geocoder.getFromLocation(latitude, longitude, 1)?.first()
                            address = addressFromGeocoder?.getAddressLine(0).toString()
                        }
                        if (address.isEmpty()) {
                            viewModel.getAddressWhenGeocoderOffline(this@MainActivity, latitude.toString(), longitude.toString())
                        } else {
                            viewModel.fetchUserOrganization(address)
                        }
                    }
                } catch(e: Exception) {
                    e.localizedMessage?.let { Log.d(TAG, it) }
                }
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            }
        }
    }

    private fun startVoiceCommandService() {
        when (
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO)
        ) {
            PackageManager.PERMISSION_GRANTED -> {
                try {
                    startService(Intent(this, LevoSonusService::class.java))
                } catch(e: Exception) {
                    e.localizedMessage?.let { Log.d(TAG, it) }
                }
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            }
        }
    }
}