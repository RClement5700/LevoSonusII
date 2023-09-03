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
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.clementcorporation.levosonusii.main.Constants.PROMPT_KEYWORD
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
private const val DEFAULT_PROMPT = "How Can I Help?"
private const val VOICE_COMMAND_KEY = "USER_INPUT"
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
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getCurrentLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
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
            bManager = LocalBroadcastManager.getInstance(this).apply {
                registerReceiver(bReceiver, intentFilter)
            }
            navController = rememberNavController()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
            viewModel.mainActivityEventsLiveData.observe(this@MainActivity) {
                if (it is MainActivityEvents.OnShowVoiceCommandActivity) {
                    onClickVoiceCommandBtn(it.title)
                }
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
                                LevoSonusNavigation(navController, showFAB,
                                    this@MainActivity, viewModel::showVoiceCommandActivity)
                                if (showFAB.value) {
                                    LSFAB {
                                        onClickVoiceCommandBtn()
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
        bManager.unregisterReceiver(bReceiver);
    }

    private fun stopService() {
        try {
            stopService(Intent(this, LevoSonusService::class.java))
        } catch(e: Exception) {
            e.localizedMessage?.let { Log.d(TAG, it) }
        }
    }

    private fun getCurrentLocation() {
        val address = mutableStateOf("")
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
                        val addressFromGeocoder = geocoder.getFromLocation(latitude, longitude, 1)?.first()
                        address.value = addressFromGeocoder?.getAddressLine(0).toString()
                        //TODO: -fetch the organization details and compare them with $address.value
                        //      -hook the app to the matching Firestore collection
                        //      --see MainActivityViewModel line 28
                        startVoiceCommandService()
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

    private fun onClickVoiceCommandBtn(prompt: String = "How Can I Help?") {
        val i = Intent(this, VoiceCommandActivity::class.java).apply {
            putExtra(PROMPT_KEYWORD, prompt)
        }
        resultLauncher.launch(i)
    }
}