package com.clementcorporation.levosonusii.screens.voiceprofile

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.clementcorporation.levosonusii.model.LSUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateVoiceProfileViewModel @Inject constructor(private val sessionDataStore: DataStore<LSUserInfo>):
    ViewModel() {
        fun getDataStore() = sessionDataStore
}