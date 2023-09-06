package com.clementcorporation.levosonusii.screens.voiceprofile

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.util.AuthenticationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceProfileViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
): ViewModel() {

    val expandMenu = mutableStateOf(false)
    val showProgressBar = mutableStateOf(false)
    val showWarningDialog = mutableStateOf(false)
    val warningDialogTitle = mutableStateOf("")

    fun signOut() {
        viewModelScope.launch {
            showProgressBar.value = true
            expandMenu.value = false
            AuthenticationUtil.signOut(sessionDataStore, voiceProfileDataStore)
        }
    }

    fun getDataStore() = sessionDataStore
    fun getVoiceProfileDataStore() = voiceProfileDataStore
}