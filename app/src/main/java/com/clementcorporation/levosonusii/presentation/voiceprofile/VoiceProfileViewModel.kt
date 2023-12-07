package com.clementcorporation.levosonusii.presentation.voiceprofile

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.VoiceProfile
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
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
            SignOutUseCase(sessionDataStore, voiceProfileDataStore).invoke()
        }
    }

    fun getDataStore() = sessionDataStore
    fun getVoiceProfileDataStore() = voiceProfileDataStore
}