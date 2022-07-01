package com.clementcorporation.levosonusii.main

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import kotlinx.coroutines.supervisorScope
import java.util.*

class LevoSonusService: Service(), RecognitionListener {
    private val TAG = "LevoSonusService"
    private val wordsSpoken = mutableStateOf("")
    private lateinit var aManager: AudioManager
    private lateinit var intentRecognizer: Intent
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate() {
        super.onCreate()
        aManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        intentRecognizer = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1000)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(this@LevoSonusService)
        }
        when {
            ContextCompat.checkSelfPermission(
                this@LevoSonusService, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED -> {
                try {
                    speechRecognizer.run {
                        startListening(intentRecognizer)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error: ${e.localizedMessage}")
                    Toast.makeText(this@LevoSonusService, "Start Listening Failed", Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> {
                requestPermissions(Activity(),
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission_group.MICROPHONE),
                    0
                )
            }
        }
        muteSystem()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
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
        //Mute the sound the phone makes when it begins listening
        //https://developer.android.com/guide/topics/media-apps/volume-and-earphones
        speechRecognizer.startListening(intentRecognizer)
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).let {
            it?.first()?.let { result ->
                wordsSpoken.value = result.trim()
//                setResult(ComponentActivity.RESULT_OK, Intent().apply {
//                    putExtra(RecognizerIntent.EXTRA_RESULTS, result)
//                    Log.e(TAG, "Result: $result")
//                })
                if (wordsSpoken.value.contentEquals("JARVIS", true)) {
                    unmuteSystem()
                    startActivity(Intent(this, VoiceCommandActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
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

    private fun muteSystem() {
        aManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
        aManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
        aManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
        aManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
        aManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
    }
    private fun unmuteSystem() {
        aManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 3, AudioManager.FLAG_PLAY_SOUND)
        aManager.setStreamVolume(AudioManager.STREAM_ALARM, 3, AudioManager.FLAG_PLAY_SOUND)
        aManager.setStreamVolume(AudioManager.STREAM_MUSIC, 3, AudioManager.FLAG_PLAY_SOUND)
        aManager.setStreamVolume(AudioManager.STREAM_RING, 3, AudioManager.FLAG_PLAY_SOUND)
        aManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 3, AudioManager.FLAG_PLAY_SOUND)
    }
}