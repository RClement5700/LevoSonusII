package com.clementcorporation.levosonusii.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.util.AuthenticationUtil
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>): ViewModel() {
    private val _mainActivityEventsState = MutableStateFlow<MainActivityEvents>(MainActivityEvents.OnFetchUserOrganization())
    val mainActivityEventsState: StateFlow<MainActivityEvents> get() = _mainActivityEventsState

    fun fetchUserOrganization(addressFromGeocoder: String) {
        viewModelScope.launch {
            var doAddressesMatch = false
            FirebaseFirestore.getInstance().collection("Organizations")
                .document("businesses").get().addOnSuccessListener { businesses ->
                    businesses.data?.forEach { business ->
                        val businessDetails = business.value as Map<*, *>
                        businessDetails.forEach { detail ->
                            if(detail.key as String == "address") {
                                val addressFromFirebase = detail.value as String
                                if (addressFromFirebase == addressFromGeocoder) {
                                    doAddressesMatch = true
                                }
                            }
                        }
                    }
                }
            _mainActivityEventsState.emit(MainActivityEvents.OnFetchUserOrganization(doAddressesMatch))
        }
    }

    fun showVoiceCommandActivity(title: String) {
        viewModelScope.launch {
            _mainActivityEventsState.emit(MainActivityEvents.OnShowVoiceCommandActivity(
                title, isTrainingMode
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