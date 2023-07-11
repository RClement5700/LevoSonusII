package com.clementcorporation.levosonusii.screens.voiceprofile

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VoiceProfileViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
    ): ViewModel()
{
    val showProgressBar = mutableStateOf(false)
    fun getDataStore() = sessionDataStore
    fun getVoiceProfileDataStore() = voiceProfileDataStore
}