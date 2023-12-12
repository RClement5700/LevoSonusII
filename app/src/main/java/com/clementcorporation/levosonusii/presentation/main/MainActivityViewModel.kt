package com.clementcorporation.levosonusii.presentation.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.VoiceProfile
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
): ViewModel() {
    private val _mainActivityUiState = MutableStateFlow<MainActivityEvents>(MainActivityEvents.OnViewModelCreated)
    val mainActivityUiState = _mainActivityUiState.asStateFlow()


    fun showVoiceCommandActivity(title: String, isTrainingMode: Boolean = false) {
        viewModelScope.launch {
            _mainActivityUiState.value =
                MainActivityEvents.OnShowVoiceCommandActivity(
                    title, isTrainingMode
                )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            SignOutUseCase().invoke(sessionDataStore, voiceProfileDataStore)
        }
    }
}

sealed class MainActivityEvents {
    data object OnViewModelCreated: MainActivityEvents()
    class OnShowVoiceCommandActivity(val title: String, val isTrainingMode: Boolean): MainActivityEvents()
}