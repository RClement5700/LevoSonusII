package com.clementcorporation.levosonusii.presentation.equipment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.ConnectionType
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
    var savedIndex by mutableIntStateOf(-1)
    var selectedIndex by mutableIntStateOf(-1)
    val showProgressBar = mutableStateOf(true)
    val expandMenu = mutableStateOf(false)
    val expandFilterSortMenu = mutableStateOf(false)
    var isHandlingDbUpdate by mutableStateOf(false)
    var equipmentIdInput by mutableStateOf("")
    var equipmentList: List<EquipmentUiModel> = emptyList()
    var mutableEquipmentList = listOf<EquipmentUiModel>()
    open val sortList = listOf(ConnectionType.BLUETOOTH, ConnectionType.WIRED)
    open val filterList = listOf(ConnectionType.BLUETOOTH, ConnectionType.WIRED)
    var selectedSortMenuIndex by mutableIntStateOf(-1)
    var selectedFilterMenuIndex by mutableIntStateOf(-1)
    var wasSortButtonClicked by mutableStateOf(false)
    var wasFilterButtonClicked by mutableStateOf(false)

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

    fun getConnectionTypeMenuItems() =
        if (wasFilterButtonClicked) {
            sortList
        } else if (wasSortButtonClicked) {
            filterList
        } else emptyList()

    fun getMenuIndex() =
        if (wasSortButtonClicked) selectedSortMenuIndex
        else if (wasFilterButtonClicked) selectedFilterMenuIndex
        else -1

    fun setMenuIndex(index: Int) {
        if (wasSortButtonClicked && selectedSortMenuIndex != index) selectedSortMenuIndex = index
        else if (wasSortButtonClicked && selectedSortMenuIndex == index) selectedSortMenuIndex = -1
        else if (wasFilterButtonClicked && selectedFilterMenuIndex != index) selectedFilterMenuIndex = index
        else if (wasFilterButtonClicked && selectedFilterMenuIndex == index) selectedFilterMenuIndex = -1
    }

    fun onMenuResetButtonClicked() {
        selectedFilterMenuIndex = -1
        selectedSortMenuIndex = -1
        savedIndex = 0
        selectedIndex = equipmentList.indexOf(mutableEquipmentList[selectedIndex])
        mutableEquipmentList = equipmentList
        _equipmentScreenUiState.value = EquipmentScreenUiState.OnDataRetrieved(equipmentList)
        expandFilterSortMenu.value = false
    }

    fun onConnectionTypeMenuApplyButtonClicked() {
        if (wasSortButtonClicked) sortByConnectionType()
        else if (wasFilterButtonClicked) filterByConnectionType()
        expandFilterSortMenu.value = false
    }

    private fun filterByConnectionType() {
        viewModelScope.launch {
            sessionDataStore.data.collect { userInfo ->
                val selectedEquipment = equipmentList[selectedIndex.takeIf { it != -1 } ?: 0]
                val connectionType = filterList[selectedFilterMenuIndex]
                mutableEquipmentList = equipmentList.filter { it.connectionType == connectionType }
                selectedIndex = mutableEquipmentList.indexOf(selectedEquipment).takeIf { it != -1 } ?: 0
                savedIndex = mutableEquipmentList.indexOf(mutableEquipmentList.find { it.serialNumber == userInfo.headsetId })
                _equipmentScreenUiState.value = EquipmentScreenUiState.OnDataRetrieved(mutableEquipmentList)
            }
        }
    }

    private fun sortByConnectionType() {
        viewModelScope.launch {
            sessionDataStore.data.collect { userInfo ->
                val selectedEquipment = mutableEquipmentList[selectedIndex.takeIf { it != -1 } ?: 0]
                val connectionType = sortList[selectedSortMenuIndex]
                mutableEquipmentList = mutableEquipmentList.sortedByDescending { it.connectionType == connectionType }
                selectedIndex = mutableEquipmentList.indexOf(selectedEquipment).takeIf { it != -1 } ?: 0
                savedIndex = mutableEquipmentList.indexOf(mutableEquipmentList.find { it.serialNumber == userInfo.headsetId })
                _equipmentScreenUiState.value = EquipmentScreenUiState.OnDataRetrieved(mutableEquipmentList)
            }
        }
    }
}