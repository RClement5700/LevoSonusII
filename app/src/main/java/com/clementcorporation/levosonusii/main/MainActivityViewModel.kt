package com.clementcorporation.levosonusii.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    val showVoiceCommandWindow = mutableStateOf(false)
    val showFab = mutableStateOf(false)
}