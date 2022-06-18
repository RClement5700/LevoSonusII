package com.clementcorporation.levosonusii.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
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
import com.clementcorporation.levosonusii.main.ui.theme.LevoSonusIITheme
import com.clementcorporation.levosonusii.navigation.LevoSonusNavigation
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        if (resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            //doSomeOperations()
            //if user says the phrase "poptarts"
            //call function poptart

            //if user says the phrase "banana"
            //call function banana
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results?.isNotEmpty() == true) Toast.makeText(applicationContext, "sound detected", Toast.LENGTH_LONG).show()
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
                                        -handle VoiceCommand in this activity:
                                            https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
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
    }

    private fun onClickVoiceCommandBtn() {
        val i = Intent(this, VoiceCommandActivity::class.java)
        resultLauncher.launch(i)
    }
}