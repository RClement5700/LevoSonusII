package com.clementcorporation.levosonusii.main

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.DEFAULT_VOICE_COMMAND_PROMPT
import com.clementcorporation.levosonusii.main.Constants.PROMPT_KEYWORD
import java.util.*

class LevoSonusService: Service(), VoiceCommandRecognitionListener {
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
                    Toast.makeText(this@LevoSonusService, getString(R.string.ls_service_offline_toast_message), Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> {
                Toast.makeText(this, getString(R.string.ls_service_failed_toast_message), Toast.LENGTH_LONG).show()
            }
        }
        muteSystem()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG, "onBind")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    override fun onError(error: Int) {
        Log.e(TAG, "Error: $error")
        speechRecognizer.startListening(intentRecognizer)
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).let {
            it?.first()?.let { result ->
                wordsSpoken.value = result.trim()
                if (wordsSpoken.value.contentEquals(VoiceProfileConstants.JARVIS.name, true)) {
                    unmuteSystem()
                    startActivity(Intent(this, VoiceCommandActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(PROMPT_KEYWORD, DEFAULT_VOICE_COMMAND_PROMPT)
                    )
                } else {
                    speechRecognizer.startListening(intentRecognizer)
                }
            }
        }
    }

    private fun muteSystem() {
        aManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.ADJUST_MUTE)
        aManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.ADJUST_MUTE)
    }
    private fun unmuteSystem() {
        aManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 4, AudioManager.ADJUST_UNMUTE)
        aManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 4, AudioManager.ADJUST_UNMUTE)
    }
}