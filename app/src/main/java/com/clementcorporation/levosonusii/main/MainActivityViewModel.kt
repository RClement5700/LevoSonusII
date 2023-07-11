package com.clementcorporation.levosonusii.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//MOVE SIGN-IN AND SIGN-OUT FUNCTIONALITY HERE

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>): ViewModel() {
    private val _mainActivityEventsLiveData = MutableLiveData<MainActivityEvents>()
    val mainActivityEventsLiveData: LiveData<MainActivityEvents> get() = _mainActivityEventsLiveData

    fun showVoiceCommandActivity(title: String) {
        viewModelScope.launch {
            _mainActivityEventsLiveData.postValue(MainActivityEvents.OnShowVoiceCommandActivity(
                title
            ))
        }
    }

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

sealed class MainActivityEvents {
    class OnShowVoiceCommandActivity(val title: String): MainActivityEvents()
}