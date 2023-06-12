package com.clementcorporation.levosonusii.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//MOVE SIGN-IN AND SIGN-OUT FUNCTIONALITY HERE

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>): ViewModel() {

    fun getUserInfo() = sessionDataStore
    suspend fun signOut() {
        Firebase.auth.signOut()
        sessionDataStore.updateData {
            LSUserInfo()
        }
        voiceProfileDataStore.updateData {
            VoiceProfile()
        }
    }
}