package com.clementcorporation.levosonusii.presentation.voice_command

import android.os.Bundle
import android.speech.RecognitionListener
import android.util.Log

private const val TAG = "VoiceCommandRecognitionListener"
interface VoiceCommandRecognitionListener: RecognitionListener {
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

    override fun onPartialResults(partialResults: Bundle?) {
        Log.e(TAG, "Received Partial Results")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.e(TAG, "Event Type: $eventType")
    }
}