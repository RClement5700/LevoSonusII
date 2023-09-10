package com.clementcorporation.levosonusii.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.Organization
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.util.AuthenticationUtil
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ORGANIZATION_PATH = "Organizations"
const val BUSINESSES_DOC = "businesses"
const val ADDRESS_KEY = "address"
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>): ViewModel() {
    private val _mainActivityEventsState = MutableStateFlow<MainActivityEvents>(MainActivityEvents.OnViewModelCreated)
    val mainActivityEventsState: StateFlow<MainActivityEvents> get() = _mainActivityEventsState

    fun fetchUserOrganization(addressFromGeocoder: String) {
        viewModelScope.launch {
            FirebaseFirestore.getInstance().collection(ORGANIZATION_PATH)
                .document(BUSINESSES_DOC).get().addOnSuccessListener { businesses ->
                    var addressFromFirebase = ""
                    var orgNameFromFirebase = ""
                    var idFromFirebase: String? = null
                    businesses.data?.forEach { business ->
                        val businessDetails = business.value as Map<*, *>
                        businessDetails.forEach { detail ->
                            when (detail.key) {
                                ADDRESS_KEY -> {
                                    if (detail.value as String == addressFromGeocoder) {
                                        addressFromFirebase = detail.value as String
                                        orgNameFromFirebase = businessDetails[Constants.NAME] as String
                                        idFromFirebase = business.key
                                    }
                                }
                            }
                        }
                    }
                    idFromFirebase?.let { id ->
                        if (id.isNotEmpty()) {
                            viewModelScope.launch {
                                sessionDataStore.updateData {
                                    it.copy(
                                        organization = Organization(
                                            id = id,
                                            name = orgNameFromFirebase,
                                            address = addressFromFirebase
                                        ),
                                        name = it.name,
                                        employeeId = it.employeeId,
                                        firebaseId = it.firebaseId,
                                        departmentId = it.departmentId,
                                        machineId = it.machineId,
                                        headsetId = it.headsetId,
                                        scannerId = it.scannerId,
                                        emailAddress = it.emailAddress,
                                        profilePicUrl = it.profilePicUrl,
                                        operatorType = it.operatorType,
                                        messengerIds = it.messengerIds
                                    )
                                }
                                _mainActivityEventsState.emit(
                                    MainActivityEvents.OnFetchUserOrganization(
                                        orgNameFromFirebase
                                    )
                                )
                            }
                        }
                    }
                }
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