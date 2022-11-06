package com.clementcorporation.levosonusii.main

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
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.clementcorporation.levosonusii.main.Constants.PROMPT_KEYWORD
import com.clementcorporation.levosonusii.main.ui.theme.LevoSonusIITheme
import com.clementcorporation.levosonusii.navigation.LevoSonusNavigation
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity(){
    private lateinit var navController: NavHostController
    private val TAG = "MainActivity"
    private lateinit var viewModel: MainActivityViewModel
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
                val userInput = intent.getStringExtra("USER_INPUT")
                userInput?.let {
                    executeVoiceCommand(it)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel = hiltViewModel()
            val intentFilter = IntentFilter().apply {
                addAction(Constants.USER_INPUT)
            }
            bManager = LocalBroadcastManager.getInstance(this).apply {
                registerReceiver(bReceiver, intentFilter)
            }
            navController = rememberNavController()
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
                                /*
                                    TODO:
                                        -hide FAB on SplashScreen
                                        -build UI for VoiceCommandWindow below
                                 */
                                val showFAB = remember {
                                    mutableStateOf(false)
                                }
                                LevoSonusNavigation(navController, showFAB)
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
        startService()
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
                    navController.navigate(LevoSonusScreens.MessagesScreen.name)
                }
                VoiceCommands.ORDERS -> {
                    navController.navigate(LevoSonusScreens.OrdersScreen.name)
                }
                VoiceCommands.SIGN_OUT -> {
                    lifecycleScope.launch {
                        viewModel.signOut()
                        delay(1000L)
                        navController.navigate(LevoSonusScreens.LoginScreen.name)
                    }
                }
                VoiceCommands.VOICE_PROFILE -> {
                    navController.navigate(LevoSonusScreens.VoiceProfileScreen.name)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startService()
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

    private fun startService() {
        when {
            ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED -> {
                try {
                    startService(Intent(this, LevoSonusService::class.java))
                } catch(e: Exception) {
                    e.localizedMessage?.let { Log.d(TAG, it) }
                }
            }
            else -> {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission_group.MICROPHONE),
                    0
                )
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