package com.clementcorporation.levosonusii.presentation.equipment.machines

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.EquipmentUiModel
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenUiState
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenViewModel
import com.clementcorporation.levosonusii.util.Constants.MACHINES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.MACHINE_ID
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MachinesScreenViewModel @Inject constructor(
    private val repo: EquipmentRepository,
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val signOutUseCase: SignOutUseCase
): EquipmentScreenViewModel(signOutUseCase, sessionDataStore) {

    var machines: List<EquipmentUiModel> = listOf()
    private val _machinesScreenUiState = MutableStateFlow<EquipmentScreenUiState>(EquipmentScreenUiState.OnLoading)
    val machinesScreenUiState = _machinesScreenUiState.asStateFlow()

    init {
        fetchMachinesData()
    }

    fun fetchMachinesData() {
        viewModelScope.launch {
            sessionDataStore.data.collect { userInfo ->
                val businessId = userInfo.organization?.id
                val equipmentId = userInfo.machineId
                if (businessId?.isBlank() == true) {
                    _machinesScreenUiState.value =
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
                                    machines = machinesData
                                    machines.find {
                                        it.serialNumber == userInfo.machineId
                                    }?.let { machine ->
                                        selectedIndex = machines.indexOf(machine)
                                    }
                                    _machinesScreenUiState.value =
                                        EquipmentScreenUiState.OnDataRetrieved(machines)
                                }
                            }

                            is Response.Error -> {
                                response.message?.let { errorMessage ->
                                    _machinesScreenUiState.value =
                                        EquipmentScreenUiState.OnFailedToLoadData(errorMessage)
                                }
                            }

                            is Response.Loading -> {
                                _machinesScreenUiState.value = EquipmentScreenUiState.OnLoading
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
            val currentMachine = machines.first()
            val selectedMachine = machines[selectedIndex]
            sessionDataStore.updateData { userInfo ->
                if (selectedIndex.toString() != userInfo.headsetId) {
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
                                _machinesScreenUiState.value = EquipmentScreenUiState.OnDataUpdated
                            }

                            is Response.Error -> {
                                response.message?.let { errorMessage ->
                                    _machinesScreenUiState.value =
                                        EquipmentScreenUiState.OnFailedToLoadData(errorMessage)
                                }
                            }

                            is Response.Loading -> {
                                _machinesScreenUiState.value = EquipmentScreenUiState.OnLoading
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
                    _machinesScreenUiState.value = EquipmentScreenUiState.OnDataRetrieved(machines)
                    userInfo.copy()
                }
            }
        }
    }
}