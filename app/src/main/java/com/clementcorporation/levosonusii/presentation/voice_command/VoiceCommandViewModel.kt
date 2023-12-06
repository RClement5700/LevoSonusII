package com.clementcorporation.levosonusii.presentation.voice_command

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class VoiceCommandViewModel: ViewModel() {
    private val _voiceCommandEventsLiveData = MutableLiveData<VoiceCommandEvents>()
    val voiceCommandsEventsLiveData: LiveData<VoiceCommandEvents> get() = _voiceCommandEventsLiveData

    fun updateTextDisplay(wordsSpoken: String) {
        viewModelScope.launch {
            _voiceCommandEventsLiveData.postValue(
                VoiceCommandEvents.OnUpdateTrainingWordsTextDisplay(
                    wordsSpoken
                )
            )
        }
    }
}

sealed class VoiceCommandEvents {
    class OnUpdateTrainingWordsTextDisplay(val wordsSpoken: String): VoiceCommandEvents()
}