package com.clementcorporation.levosonusii.presentation.equipment.scanners

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenUiState
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenViewModel
import com.clementcorporation.levosonusii.util.Constants.SCANNERS_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.SCANNER_ID
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannersScreenViewModel @Inject constructor(
    private val repo: EquipmentRepository,
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val signOutUseCase: SignOutUseCase
): EquipmentScreenViewModel(signOutUseCase, sessionDataStore) {

    init {
        fetchScannersData()
    }

    fun fetchScannersData() {
        viewModelScope.launch {
            sessionDataStore.data.collect { userInfo ->
                val businessId = userInfo.organization?.id
                val equipmentId = userInfo.scannerId
                if (businessId?.isBlank() == true) {
                    _equipmentScreenUiState.value =
                        EquipmentScreenUiState.OnFailedToLoadData("Invalid business ID. Please sign in again.")
                    return@collect
                }
                businessId?.let {
                    repo.getEquipment(
                        businessId = businessId,
                        equipmentId = equipmentId,
                        equipmentEndpoint = SCANNERS_ENDPOINT
                    ).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                response.data?.let { scannersData ->
                                    equipmentList = scannersData
                                    equipmentList.find {
                                        it.serialNumber == userInfo.scannerId
                                    }?.let { scanner ->
                                        selectedIndex = equipmentList.indexOf(scanner)
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
            val currentScanner = equipmentList.first()
            val selectedScanner = equipmentList[selectedIndex]
            sessionDataStore.updateData { userInfo ->
                if (selectedIndex != 0) {
                    repo.setEquipmentId(
                        businessId = userInfo.organization?.id.orEmpty(),
                        firebaseId = userInfo.firebaseId,
                        equipmentKey = SCANNER_ID,
                        currentEquipment = currentScanner,
                        newEquipment = selectedScanner,
                        equipmentEndpoint = SCANNERS_ENDPOINT
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
                        machineId = userInfo.machineId,
                        scannerId = selectedScanner.serialNumber,
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