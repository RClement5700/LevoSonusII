package com.clementcorporation.levosonusii.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.clementcorporation.levosonusii.main.Constants.PROMPT_KEYWORD
import com.clementcorporation.levosonusii.main.ui.theme.LevoSonusIITheme
import com.clementcorporation.levosonusii.navigation.LevoSonusNavigation
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity(){
    private val TAG = "MainActivity"
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    val resultCode = result.resultCode
        if (resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            //doSomeOperations()
            val results = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
        }
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainActivityViewModel = hiltViewModel()
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
                                val navController = rememberNavController()
                                val showFAB = remember {
                                    mutableStateOf(!navController.currentDestination?.route.contentEquals(LevoSonusScreens.SplashScreen.name))
                                }
                                LevoSonusNavigation()
                                Log.d("","current destination: ${navController.currentDestination?.route}")
                                Log.d("","screen name == current destination: ${navController.currentDestination?.route.contentEquals(LevoSonusScreens.SplashScreen.name)}")
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

    override fun onResume() {
        super.onResume()
        startService()
    }

    override fun onStop() {
        super.onStop()
        stopService()
    }

    private fun stopService() {
        try {
            stopService(Intent(this, LevoSonusService::class.java))
        } catch(e: Exception) {
            e.localizedMessage?.let { Log.d(TAG, it) }
        }
    }

    private fun startService() {
        try {
            startService(Intent(this, LevoSonusService::class.java))
        } catch(e: Exception) {
            e.localizedMessage?.let { Log.d(TAG, it) }
        }
    }

    private fun onClickVoiceCommandBtn(prompt: String = "How Can I Help?") {
        val i = Intent(this, VoiceCommandActivity::class.java).apply {
            putExtra(PROMPT_KEYWORD, prompt)
        }
        resultLauncher.launch(i)
    }
}