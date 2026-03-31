package com.clementcorporation.levosonusii.presentation.equipment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.EquipmentUiModel
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EquipmentScreenUiState {
    data object OnLoading: EquipmentScreenUiState()
    data class OnDataRetrieved(val data: List<EquipmentUiModel>): EquipmentScreenUiState()
    data object OnDataUpdated: EquipmentScreenUiState()
    data class OnFailedToLoadData(val message: String): EquipmentScreenUiState()
}
@HiltViewModel
open class EquipmentScreenViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val sessionDateStore: DataStore<LSUserInfo>
): ViewModel() {
    var selectedIndex by mutableIntStateOf(-1)
    val showProgressBar = mutableStateOf(true)
    val expandMenu = mutableStateOf(false)
    var isHandlingDbUpdate by mutableStateOf(false)

    fun signOut(navigate: () -> Unit) {
        viewModelScope.launch {
            showProgressBar.value = true
            expandMenu.value = false
            signOutUseCase.invoke(navigate)
        }
    }

    fun getSessionDataStore() = sessionDateStore
}