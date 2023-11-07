package com.clementcorporation.levosonusii.main

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.clementcorporation.levosonusii.BuildConfig
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.Organization
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.util.AuthenticationUtil
import com.clementcorporation.levosonusii.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ORGANIZATION_PATH = "Organizations"
const val BUSINESSES_DOC = "businesses"
const val ADDRESS_KEY = "address"
const val RESULTS_KEY = "results"
const val FORMATTED_ADDRESS_KEY = "formatted_address"
private const val TAG = "MainActivityViewModel"
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val resources: Resources,
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>): ViewModel() {
    private val _mainActivityEventsState = MutableStateFlow<MainActivityEvents>(MainActivityEvents.OnViewModelCreated)
    val mainActivityEventsState = _mainActivityEventsState.asStateFlow()

    fun getAddressWhenGeocoderOffline(context: Context, lat: String, long: String) {
        viewModelScope.launch {
            var address = ""
            val queue = Volley.newRequestQueue(context)
            val url = resources.getString(R.string.google_maps_location_api_url, lat, long, BuildConfig.PLACES_API_KEY)
            val jsonRequest = JsonObjectRequest(
                url,
                { response ->
                    address = response.getJSONArray(RESULTS_KEY)
                        .getJSONObject(0)
                        .getString(FORMATTED_ADDRESS_KEY)
                    Log.e(TAG, "Current Location Received: $address")
                    fetchUserOrganization(address)
                },
                {
                    Log.e(TAG, "Current Location Error: ${it.message}")
                })
            queue.add(jsonRequest)
            queue.start()
        }
    }

    private fun fetchOrganizationCallback(addressFromGeocoder: String): Flow<Resource<Organization>> =
        callbackFlow {
            FirebaseFirestore.getInstance().collection(ORGANIZATION_PATH)
                .document(BUSINESSES_DOC).get().addOnSuccessListener { businesses ->
                var addressFromFirebase = ""
                var orgNameFromFirebase = ""
                var idFromFirebase = ""
                businesses.data?.forEach { business ->
                    val businessDetails = business.value as Map<*, *>
                    val address = businessDetails[ADDRESS_KEY] as String
                    if (address == addressFromGeocoder) {
                        addressFromFirebase = address
                        orgNameFromFirebase = businessDetails[Constants.NAME] as String
                        idFromFirebase = business.key
                    }
                }
                trySend(
                    Resource.Success(
                        Organization(
                            id = idFromFirebase,
                            name = orgNameFromFirebase,
                            address = addressFromFirebase
                        )
                    )
                )
                close()
            }
            awaitClose {
                cancel()
            }
        }

    fun fetchUserOrganization(addressFromGeocoder: String) {
        viewModelScope.launch {
            fetchOrganizationCallback(addressFromGeocoder).collectLatest { callback ->
                callback.data?.let { organization ->
                    sessionDataStore.updateData {
                        it.copy(
                            organization = organization,
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
                        MainActivityEvents.OnFetchUserOrganization(organization.name)
                    )
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