package com.clementcorporation.levosonusii.presentation.voice_command

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
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
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.data.local.LevoSonusService
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.PADDING
import com.clementcorporation.levosonusii.util.Constants.PROMPT_KEYWORD
import com.clementcorporation.levosonusii.util.Constants.USER_INPUT
import com.clementcorporation.levosonusii.util.Constants.VOICE_COMMAND_KEY
import com.clementcorporation.levosonusii.util.LevoSonusLogo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class VoiceCommandActivity: ComponentActivity(), VoiceCommandRecognitionListener {
    private val TAG = "VoiceCommandActivity"
    private val wordsSpoken = mutableStateOf("")
    private val wordsSpokenArray = arrayListOf<String>()
    private lateinit var intentRecognizer: Intent
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tts: TextToSpeech
    private lateinit var prompt: String
    private lateinit var viewModel: VoiceCommandViewModel
    private var isTrainingMode = false

    @Composable
    fun VoiceCommandWindow() {
        viewModel = viewModel()
        viewModel.voiceCommandsEventsLiveData.observe(this@VoiceCommandActivity) {
            if (it is VoiceCommandEvents.OnUpdateTrainingWordsTextDisplay) {
                wordsSpoken.value = it.wordsSpoken
            }
        }
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
                .size(36.dp)
                .zIndex(1f), onClick = { finish() }) {
                Icon(
                    imageVector = Icons.Filled.Cancel, 
                    contentDescription = stringResource(id = R.string.voice_command_close_button_content_description), 
                    tint = Color.White
                )
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

        @Deprecated("Deprecated Utterance Progress Listener initialized")
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
        isTrainingMode = intent.getBooleanExtra(IS_TRAINING_MODE, false)
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
                    Toast.makeText(this@VoiceCommandActivity, getString(R.string.ls_service_failed_toast_message), Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission_group.MICROPHONE), 0)
            }
        }
    }

    override fun onError(error: Int) {
        Log.e(TAG, "Error: $error")
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).let {
            it?.first()?.let { result ->
                wordsSpoken.value += if (wordsSpoken.value.isEmpty()) result.trim() else " • ${result.trim()}"
                setResult(RESULT_OK, Intent().apply {
                    putExtra(RecognizerIntent.EXTRA_RESULTS, result)
                    Log.e(TAG, "Voice Command Result: $result")
                })
                speechRecognizer.stopListening()
                finish()
                lifecycleScope.launch {
                    delay(1000L)
                    if (isTrainingMode && wordsSpokenArray.size < 5) {
                        wordsSpokenArray.add(result.trim())
                        viewModel.updateTextDisplay(wordsSpoken.value)
                        onResults(results)
                    } else {
                        val userInput = Intent(USER_INPUT)
                        userInput.putExtra(VOICE_COMMAND_KEY, wordsSpoken.value)
                        LocalBroadcastManager.getInstance(this@VoiceCommandActivity).sendBroadcast(userInput)
                        speechRecognizer.stopListening()
                        finish()
                    }
                }
            }
        }
    }

    companion object {
        const val IS_TRAINING_MODE = "IS_TRAINING_MODE"
    }
}