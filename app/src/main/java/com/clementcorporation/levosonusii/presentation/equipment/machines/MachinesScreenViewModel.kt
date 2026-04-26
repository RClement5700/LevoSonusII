package com.clementcorporation.levosonusii.presentation.equipment.machines

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.MachineType
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenUiState
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenViewModel
import com.clementcorporation.levosonusii.util.Constants.MACHINES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.MACHINE_ID
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MachinesScreenViewModel @Inject constructor(
    private val repo: EquipmentRepository,
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val signOutUseCase: SignOutUseCase
): EquipmentScreenViewModel(signOutUseCase, sessionDataStore) {
    val sortList = listOf(MachineType.ElectricPalletJack.label, MachineType.Forklift.label)
    val filterList = listOf(MachineType.ElectricPalletJack.label, MachineType.Forklift.label)
    var selectedSortMenuIndex by mutableIntStateOf(-1)
    var selectedFilterMenuIndex by mutableIntStateOf(-1)
    var wasSortButtonClicked by mutableStateOf(false)
    var wasFilterButtonClicked by mutableStateOf(false)

    val expandMachineTypeMenu = mutableStateOf(false)

    init {
        fetchMachinesData()
    }

    fun getMenuItems() =
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

    fun filterByMachineType(machineType: MachineType? = null) {
        _equipmentScreenUiState.value = EquipmentScreenUiState.OnDataRetrieved(
            machineType?.let {
                equipmentList.filter { it.machineType == machineType }
            } ?: run {
                equipmentList
            }
        )
    }

    fun fetchMachinesData() {
        viewModelScope.launch {
            sessionDataStore.data.collect { userInfo ->
                val businessId = userInfo.organization?.id
                val equipmentId = userInfo.machineId
                if (businessId?.isBlank() == true) {
                    _equipmentScreenUiState.value =
                        EquipmentScreenUiState.OnFailedToLoadData("Invalid business ID. Please sign in again.")
                    return@collect
                }
                businessId?.let {
                    repo.getEquipment(
                        businessId = businessId,
                        equipmentId = equipmentId,
                        equipmentEndpoint = MACHINES_ENDPOINT
                    ).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                response.data?.let { machinesData ->
                                    equipmentList = machinesData
                                    equipmentList.find {
                                        it.serialNumber == userInfo.machineId
                                    }?.let { machine ->
                                        selectedIndex = equipmentList.indexOf(machine)
                                    }
                                    _equipmentScreenUiState.value =
                                        EquipmentScreenUiState.OnDataRetrieved(equipmentList)
                                }
                            }

                            is Response.Error -> {
                                response.message?.let { errorMessage ->
                                    _equipmentScreenUiState.value =
                                        EquipmentScreenUiState.OnFailedToLoadData(errorMessage)
                                }
                            }

                            is Response.Loading -> {
                                _equipmentScreenUiState.value = EquipmentScreenUiState.OnLoading
                            }
                        }
                    }
                }
            }
        }
    }

    fun onApplyButtonClicked() {
        viewModelScope.launch {
            isHandlingDbUpdate = true
            val currentMachine = equipmentList.first()
            val selectedMachine = equipmentList[selectedIndex]
            sessionDataStore.updateData { userInfo ->
                if (selectedIndex != 0) {
                    repo.setEquipmentId(
                        businessId = userInfo.organization?.id.orEmpty(),
                        firebaseId = userInfo.firebaseId,
                        equipmentKey = MACHINE_ID,
                        currentEquipment = currentMachine,
                        newEquipment = selectedMachine,
                        equipmentEndpoint = MACHINES_ENDPOINT
                    ).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                _equipmentScreenUiState.value = EquipmentScreenUiState.OnDataUpdated
                            }

                            is Response.Error -> {
                                response.message?.let { errorMessage ->
                                    _equipmentScreenUiState.value =
                                        EquipmentScreenUiState.OnFailedToLoadData(errorMessage)
                                }
                            }

                            is Response.Loading -> {
                                _equipmentScreenUiState.value = EquipmentScreenUiState.OnLoading
                            }
                        }
                    }
                    userInfo.copy(
                        organization = userInfo.organization,
                        employeeId = userInfo.employeeId,
                        firebaseId = userInfo.firebaseId,
                        name = userInfo.name,
                        emailAddress = userInfo.emailAddress,
                        password = userInfo.password,
                        profilePicUrl = userInfo.profilePicUrl,
                        machineId = selectedMachine.serialNumber,
                        scannerId = userInfo.scannerId,
                        headsetId = userInfo.headsetId,
                        departmentId = userInfo.departmentId,
                        operatorType = userInfo.operatorType,
                        messengerIds = arrayListOf(),
                        voiceProfile = hashMapOf()
                    )
                } else {
                    _equipmentScreenUiState.value = EquipmentScreenUiState.OnDataRetrieved(equipmentList)
                    userInfo.copy()
                }
            }
            isHandlingDbUpdate = false
        }
    }
}