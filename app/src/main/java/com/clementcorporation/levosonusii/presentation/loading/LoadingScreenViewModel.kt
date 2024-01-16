package com.clementcorporation.levosonusii.presentation.loading

import android.content.res.Resources
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.clementcorporation.levosonusii.BuildConfig
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.LoadingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val _loadingScreenUiState = MutableStateFlow<LoadingScreenUiState>(
        LoadingScreenUiState.OnLoading
    )
    val loadingScreenUiState = _loadingScreenUiState.asStateFlow()
    var address by mutableStateOf("")

    fun getAddressWhenGeocoderOffline(queue: RequestQueue, lat: String, long: String) {
        val url = resources.getString(R.string.google_maps_location_api_url, lat, long, BuildConfig.PLACES_API_KEY)
        val jsonRequest = JsonObjectRequest(
            url,
            { response ->
                address = response.getJSONArray(RESULTS_KEY)
                    .getJSONObject(0)
                    .getString(FORMATTED_ADDRESS_KEY)
                Log.e(TAG, "Current Location Received: $address")
                getBusinessByAddress()
                queue.stop()
            },
            {
                Log.e(TAG, "Current Location Error: ${it.message}")
                getBusinessByAddress()
            })
        queue.add(jsonRequest)
        queue.start()
    }

    fun getBusinessByAddress() {
        repo.getBusinessByAddress(address).onEach { business ->
            _loadingScreenUiState.value = if (business != null) {
                Log.d(TAG, "Business retrieved: ${business.name}")
                sessionDataStore.updateData {
                    it.copy(
                        organization = business,
                        name = "",
                        employeeId = "",
                        firebaseId = "",
                        departmentId = "",
                        machineId = "",
                        headsetId = "",
                        scannerId = "",
                        emailAddress = "",
                        profilePicUrl = "",
                        operatorType = "",
                        messengerIds = arrayListOf()
                    )
                }
                LoadingScreenUiState.OnFetchUsersBusiness(business.name)
            } else LoadingScreenUiState.OnFailedToRetrieveBusiness
        }.launchIn(scope).invokeOnCompletion { scope.cancel() }
    }
}

sealed class LoadingScreenUiState {
    data object OnLoading: LoadingScreenUiState()
    class OnFetchUsersBusiness(val name: String): LoadingScreenUiState()
    data object OnFailedToRetrieveBusiness: LoadingScreenUiState()
}