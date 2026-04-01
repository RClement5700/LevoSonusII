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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val sessionDataStore: DataStore<LSUserInfo>
): ViewModel() {
    var selectedIndex by mutableIntStateOf(-1)
    val showProgressBar = mutableStateOf(true)
    val expandMenu = mutableStateOf(false)
    var isHandlingDbUpdate by mutableStateOf(false)
    var equipmentIdInput by mutableStateOf("")
    var equipmentList: List<EquipmentUiModel> = emptyList()

    internal val _equipmentScreenUiState = MutableStateFlow<EquipmentScreenUiState>(EquipmentScreenUiState.OnLoading)
    val equipmentScreenUiState = _equipmentScreenUiState.asStateFlow()

    private var searchJob: Job? = null

    fun signOut(navigate: () -> Unit) {
        viewModelScope.launch {
            showProgressBar.value = true
            expandMenu.value = false
            signOutUseCase.invoke(navigate)
        }
    }

    fun getSessionDataStore() = sessionDataStore

    fun onQueryChange() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            val currentEquipment = equipmentList.first()
            val query = equipmentIdInput.trim()
            if (query.isEmpty()) {
                _equipmentScreenUiState.value = EquipmentScreenUiState.OnDataRetrieved(equipmentList)
            } else {
                val matches = equipmentList.subList(1, equipmentList.size)
                    .filter { it.serialNumber.contains(query) }
                _equipmentScreenUiState.value = EquipmentScreenUiState.OnDataRetrieved(
                    matches.ifEmpty { listOf(currentEquipment) }.toMutableList().also { mutableList ->
                        mutableList.add(0, currentEquipment)
                    }.distinctBy { it.serialNumber }
                )
            }
        }
    }
}