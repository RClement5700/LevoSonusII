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
class MainActivity : ComponentActivity(), RecognitionListener {
    private val TAG = "MainActivity"
    private val wordsSpoken = mutableStateOf("")
    private lateinit var intentRecognizer: Intent
    private lateinit var speechRecognizer: SpeechRecognizer
//    private lateinit var tts: TextToSpeech
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

    override fun onReadyForSpeech(params: Bundle?) {
        Log.e(TAG, "Ready for Speech Input")
    }

    override fun onBeginningOfSpeech() {
        Log.e(TAG, "Speech Beginning")
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.e(TAG,"Buffer Received: $buffer")
    }

    override fun onEndOfSpeech() {
        Log.e(TAG, "End of Speech")
    }

    override fun onError(error: Int) {
        Log.e(TAG, "Error: $error")
        speechRecognizer.startListening(intentRecognizer)
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).let {
            it?.first()?.let { result ->
                wordsSpoken.value = result.trim()
                setResult(RESULT_OK, Intent().apply {
                    putExtra(RecognizerIntent.EXTRA_RESULTS, result)
                    Log.e(TAG, "Result: $result")
                })
                if (wordsSpoken.value.contentEquals("JARVIS", true)) {
                    onClickVoiceCommandBtn()
                } else {
                    speechRecognizer.startListening(intentRecognizer)
                }
            }
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.e(TAG, "Event Type: $eventType")
    }
}