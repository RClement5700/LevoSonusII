package com.clementcorporation.levosonusii.presentation.loading

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
import com.clementcorporation.levosonusii.domain.repositories.LoadingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

const val RESULTS_KEY = "results"
const val FORMATTED_ADDRESS_KEY = "formatted_address"
private const val TAG = "LoadingScreenViewModel"

@HiltViewModel
class LoadingScreenViewModel @Inject constructor(
    private val repo: LoadingRepository,
    private val resources: Resources,
    private val sessionDataStore: DataStore<LSUserInfo>
): ViewModel() {
    private val _loadingScreenUiState = MutableStateFlow<LoadingScreenUiState>(
        LoadingScreenUiState.OnLoading
    )
    val loadingScreenUiState = _loadingScreenUiState.asStateFlow()

    fun getAddressWhenGeocoderOffline(queue: RequestQueue, lat: String, long: String) {
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

    fun getBusinessByAddress(addressFromGeocoder: String) {
        viewModelScope.launch {
            repo.getBusinessByAddress(addressFromGeocoder).collectLatest { business ->
                _loadingScreenUiState.value =
                    if (business != null) {
                        Log.d(TAG, "Business retrieved: ${business.name}")
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
                        LoadingScreenUiState.OnFetchUsersBusiness(business.name)
                    }
                    else LoadingScreenUiState.OnFailedToRetrieveBusiness
            }
        }
    }
}

sealed class LoadingScreenUiState {
    data object OnLoading: LoadingScreenUiState()
    class OnFetchUsersBusiness(val name: String): LoadingScreenUiState()
    data object OnFailedToRetrieveBusiness: LoadingScreenUiState()
}