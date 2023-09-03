package com.clementcorporation.levosonusii.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.util.AuthenticationUtil
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>): ViewModel() {
    private val _mainActivityEventsLiveData = MutableLiveData<MainActivityEvents>()
    val mainActivityEventsLiveData: LiveData<MainActivityEvents> get() = _mainActivityEventsLiveData

    init {
        fetchUserOrganization()
    }

    private fun fetchUserOrganization() {
        viewModelScope.launch {
            val organizations = arrayListOf<String>()
            FirebaseFirestore.getInstance().collection("Organizations")
                .document("businesses").get().addOnSuccessListener { businesses ->
                    businesses.data?.forEach { business ->
                        when (business.key) {
                            "name" -> organizations.add(business.value as String)
                        }
                    }
                }
        }
    }

    fun showVoiceCommandActivity(title: String) {
        viewModelScope.launch {
            _mainActivityEventsLiveData.postValue(MainActivityEvents.OnShowVoiceCommandActivity(
                title
            ))
        }
    }

    fun signOut() {
        viewModelScope.launch {
            AuthenticationUtil.signOut(sessionDataStore, voiceProfileDataStore)
        }
    }
}

sealed class MainActivityEvents {
    class OnShowVoiceCommandActivity(val title: String): MainActivityEvents()
}