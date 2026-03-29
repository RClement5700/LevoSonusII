package com.clementcorporation.levosonusii.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.use_cases.SessionDataStoreUseCase
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeScreenUiEvents {
    data object Loading: HomeScreenUiEvents()
    data class Error(val message: String): HomeScreenUiEvents()
    data class DataRetrieved(val data: LSUserInfo): HomeScreenUiEvents()
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStoreUseCase,
    private val signOutUseCase: SignOutUseCase
): ViewModel() {
    val expandMenu = mutableStateOf(false)
    var username by mutableStateOf("")
    var showProgressBar by mutableStateOf(true)
    var showAppBar by mutableStateOf(false)
    var inflateProfilePic by mutableStateOf(false)
    private val _homeScreenEventsStateFlow = MutableStateFlow<HomeScreenUiEvents>(HomeScreenUiEvents.Loading)
    val homeScreenEventsStateFlow = _homeScreenEventsStateFlow.asStateFlow()

    init {
        getSessionDataStore()
    }

    fun signOut(navigate: () -> Unit) {
        viewModelScope.launch {
            showProgressBar = true
            expandMenu.value = false
            signOutUseCase.invoke(navigate)
        }
    }

    fun getSessionDataStore() = viewModelScope.launch {
        sessionDataStore().collect { response ->
            when(response) {
                is Response.Loading -> {
                    showProgressBar = true
                    showAppBar = false
                    _homeScreenEventsStateFlow.value = HomeScreenUiEvents.Loading
                }
                is Response.Success -> {
                    showProgressBar = false
                    showAppBar = true
                    response.data?.let { userInfo ->
                        username = userInfo.name
                        _homeScreenEventsStateFlow.value = HomeScreenUiEvents.DataRetrieved(userInfo)
                    }
                }
                is Response.Error -> {
                    showProgressBar = false
                    showAppBar = false
                    _homeScreenEventsStateFlow.value = HomeScreenUiEvents.Error("Failed to retrieve user data")
                }
            }
        }
    }
}