package com.clementcorporation.levosonusii.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
    ): ViewModel()
{
    val expandMenu = mutableStateOf(false)
    val showProgressBar = mutableStateOf(false)

    fun getVoiceProfile() = voiceProfileDataStore
    fun getUserInfo() = sessionDataStore
    suspend fun signOut() {
        Firebase.auth.signOut()
        sessionDataStore.updateData {
            LSUserInfo()
        }
        voiceProfileDataStore.updateData {
            VoiceProfile()
        }
        showProgressBar.value = true
        expandMenu.value = false
    }
}