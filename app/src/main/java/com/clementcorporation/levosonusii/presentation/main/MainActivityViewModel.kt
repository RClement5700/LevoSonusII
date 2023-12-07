package com.clementcorporation.levosonusii.presentation.main

import android.content.res.Resources
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.clementcorporation.levosonusii.BuildConfig
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.VoiceProfile
import com.clementcorporation.levosonusii.domain.repositories.MainRepository
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

const val BUSINESSES_ENDPOINT = "businesses"
const val RESULTS_KEY = "results"
const val FORMATTED_ADDRESS_KEY = "formatted_address"
private const val TAG = "MainActivityViewModel"
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repo: MainRepository,
    private val resources: Resources,
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
): ViewModel() {
    private val _mainActivityEventsState = MutableStateFlow<MainActivityEvents>(MainActivityEvents.OnViewModelCreated)
    val mainActivityEventsState = _mainActivityEventsState.asStateFlow()

    fun getAddressWhenGeocoderOffline(queue: RequestQueue, lat: String, long: String) {
        viewModelScope.launch {
            var address: String?
            val url = resources.getString(R.string.google_maps_location_api_url, lat, long, BuildConfig.PLACES_API_KEY)
            val jsonRequest = JsonObjectRequest(
                url,
                { response ->
                    address = response.getJSONArray(RESULTS_KEY)
                        .getJSONObject(0)
                        .getString(FORMATTED_ADDRESS_KEY)
                    Log.e(TAG, "Current Location Received: $address")
                    getBusinessByAddress(address ?: "")
                    queue.stop()
                },
                {
                    Log.e(TAG, "Current Location Error: ${it.message}")
                })
            queue.add(jsonRequest)
            queue.start()
        }
    }

    fun getBusinessByAddress(addressFromGeocoder: String) {
        //isExecuting??
        viewModelScope.launch {
            repo.getBusinessByAddress(addressFromGeocoder).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        val result = response.data
                        result?.let { business ->
                            _mainActivityEventsState.value =
                                MainActivityEvents.OnFetchUsersBusiness(business.name)
                            sessionDataStore.updateData {
                                it.copy(
                                    organization = business,
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
                        }
                    }

                    is Response.Error -> {
                        val errorMessage = response.message
                        errorMessage?.let {
                            Log.e(TAG, it)
                        }
                        _mainActivityEventsState.value =
                            MainActivityEvents.OnFailedToRetrieveBusiness
                    }
                }
            }
        }
    }

    fun showVoiceCommandActivity(title: String, isTrainingMode: Boolean = false) {
        viewModelScope.launch {
            _mainActivityEventsState.value =
                MainActivityEvents.OnShowVoiceCommandActivity(
                    title, isTrainingMode
                )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            SignOutUseCase(sessionDataStore, voiceProfileDataStore).invoke()
        }
    }
}

sealed class MainActivityEvents {
    data object OnViewModelCreated: MainActivityEvents()
    class OnFetchUsersBusiness(val name: String? = null): MainActivityEvents()
    data object OnFailedToRetrieveBusiness: MainActivityEvents()
    class OnShowVoiceCommandActivity(val title: String, val isTrainingMode: Boolean): MainActivityEvents()
}