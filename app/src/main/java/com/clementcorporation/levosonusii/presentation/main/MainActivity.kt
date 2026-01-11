package com.clementcorporation.levosonusii.presentation.main

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.clementcorporation.levosonusii.data.local.LevoSonusService
import com.clementcorporation.levosonusii.navigation.LevoSonusNavigation
import com.clementcorporation.levosonusii.presentation.voice_command.VoiceCommandActivity
import com.clementcorporation.levosonusii.ui.theme.LevoSonusIITheme
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.Constants.DEFAULT_VOICE_COMMAND_PROMPT
import com.clementcorporation.levosonusii.util.Constants.PROMPT_KEYWORD
import com.clementcorporation.levosonusii.util.Constants.VOICE_COMMAND_KEY
import com.clementcorporation.levosonusii.util.LSFAB
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.clementcorporation.levosonusii.util.VoiceCommands
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"
@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity(){
    private lateinit var navController: NavHostController
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permission ->
        if (!permission) {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
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
        checkForLocationPermission()
        setContent {
            val intentFilter = IntentFilter().apply {
                addAction(Constants.USER_INPUT)
            }
            viewModel = hiltViewModel()
            val uiState: MainActivityEvents = viewModel.mainActivityUiState.collectAsStateWithLifecycle().value
            bManager = LocalBroadcastManager.getInstance(this).apply {
                registerReceiver(bReceiver, intentFilter)
            }
            navController = rememberNavController()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            when(uiState) {
                is MainActivityEvents.OnShowVoiceCommandActivity -> {
                    val i = Intent(this@MainActivity, VoiceCommandActivity::class.java).apply {
                        putExtra(PROMPT_KEYWORD, uiState.title)
                        putExtra(VoiceCommandActivity.IS_TRAINING_MODE, uiState.isTrainingMode)
                    }
                    resultLauncher.launch(i)
                }
                else -> {}
            }
            LevoSonusIITheme {
                Box(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .fillMaxSize()
                        .padding(8.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    val showFAB = remember {
                        mutableStateOf(false)
                    }
                    LevoSonusNavigation(
                        showFab = showFAB,
                        navController = navController,
                        showVoiceCommandActivity = viewModel::showVoiceCommandActivity,
                    )
                    if (showFAB.value) {
                        LSFAB {
                            viewModel.showVoiceCommandActivity(DEFAULT_VOICE_COMMAND_PROMPT)
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
                    viewModel.signOut {
                        navController.navigate(LevoSonusScreens.LoginScreen.name)
                    }
                }
                VoiceCommands.VOICE_PROFILE -> {
                    navController.navigate(LevoSonusScreens.VoiceProfileScreen.name)
                }
            }
        }
    }

    private fun stopVoiceCommandService() {
        try {
            stopService(Intent(this, LevoSonusService::class.java))
        } catch(e: Exception) {
            e.localizedMessage?.let { Log.d(TAG, it) }
        }
    }

    private fun checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
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