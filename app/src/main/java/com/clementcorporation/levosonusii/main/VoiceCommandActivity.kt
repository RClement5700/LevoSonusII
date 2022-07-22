package com.clementcorporation.levosonusii.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.Constants.PROMPT_KEYWORD
import com.clementcorporation.levosonusii.main.Constants.USER_INPUT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class VoiceCommandActivity: ComponentActivity(), RecognitionListener {
    private val TAG = "VoiceCommandActivity"
    private val wordsSpoken = mutableStateOf("")
    private lateinit var intentRecognizer: Intent
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tts: TextToSpeech
    private lateinit var prompt: String

    @Composable
    fun VoiceCommandWindow() {
        var width = 1f
        var height = 0.75f
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = height
            height = 0.75f
        }
        Box(
            modifier = Modifier.clickable {
                finish()
            },
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(modifier = Modifier
                .size(25.dp)
                .zIndex(1f), onClick = { finish() }) {
                Icon(imageVector = Icons.Filled.Cancel, contentDescription = "Close", tint = Color.Red)
            }
            Card(
                modifier = Modifier
                    .clickable {
                        finish()
                    }
                    .fillMaxWidth(width)
                    .fillMaxHeight(height)
                    .padding(PADDING.dp),
                shape = RoundedCornerShape(Constants.CURVATURE.dp),
                backgroundColor = Color.Transparent.copy(0.8f),
                elevation = ELEVATION.dp
            ) {
                Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(modifier = Modifier.padding(PADDING.dp), color = Color.White, text = prompt)
                    LevoSonusLogo(size = 50.dp, showText = true)
                    Text(modifier = Modifier.padding(PADDING.dp), color = Color.White, text = wordsSpoken.value)
                }
            }
        }
    }

    inner class LSUtteranceProgressListener: UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            Log.e(TAG, "Utterance Listener: START")
        }

        override fun onDone(utteranceId: String?) {
            Log.e(TAG, "Utterance Listener: DONE")
        }

        override fun onError(utteranceId: String?) {
            Log.e(TAG,"Utterance Listener: ERROR")
            onStart(utteranceId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            stopService(Intent(this, LevoSonusService::class.java))
        } catch(e: Exception) {
            e.localizedMessage?.let { Log.d(TAG, it) }
        }
        setContent {
            VoiceCommandWindow()
        }
        prompt = intent.getStringExtra(PROMPT_KEYWORD) as String
        intentRecognizer = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1000)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(this@VoiceCommandActivity)
        }
        tts = TextToSpeech(this@VoiceCommandActivity) {
            when (it) {
                TextToSpeech.SUCCESS -> {
                    tts.language = Locale.US
                    tts.setOnUtteranceProgressListener(LSUtteranceProgressListener())
                    tts.speak(
                        prompt, TextToSpeech.QUEUE_FLUSH,
                        null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
                    )
                }
            }
        }
        when {
            ContextCompat.checkSelfPermission(this@VoiceCommandActivity, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED -> {
                try {
                    speechRecognizer.run {
                        stopListening()
                        startListening(intentRecognizer)
                    }
                } catch(e: Exception) {
                    Log.e(TAG, "Error: ${e.localizedMessage}")
                    Toast.makeText(this@VoiceCommandActivity, "Start Listening Failed", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission_group.MICROPHONE), 0)
            }
        }
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
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).let {
            it?.first()?.let { result ->
                wordsSpoken.value = result.trim()
                setResult(RESULT_OK, Intent().apply {
                    putExtra(RecognizerIntent.EXTRA_RESULTS, result)
                    Log.e(TAG, "Result: $result")
                })
                val userInput = Intent(USER_INPUT)
                userInput.putExtra("USER_INPUT", wordsSpoken.value)
                LocalBroadcastManager.getInstance(this).sendBroadcast(userInput)
                speechRecognizer.stopListening()
                lifecycleScope.launch {
                    delay(1000L)
                    finish()
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