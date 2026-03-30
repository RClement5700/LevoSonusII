package com.clementcorporation.levosonusii.presentation.equipment.headsets

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.EquipmentUiModel
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenUiState
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenViewModel
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeadsetsScreenViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val repo: EquipmentRepository,
    private val signOutUseCase: SignOutUseCase
): EquipmentScreenViewModel(signOutUseCase) {
    var headsets: List<EquipmentUiModel> = listOf()
    private val _headsetsScreenUiState = MutableStateFlow<EquipmentScreenUiState>(EquipmentScreenUiState.OnLoading)
    val headsetsScreenUiState = _headsetsScreenUiState.asStateFlow()

    init {
        fetchHeadsetsData()
    }

    fun fetchHeadsetsData() {
        viewModelScope.launch {
            sessionDataStore.data.collect { userInfo ->
                val businessId = userInfo.organization?.id
                val equipmentId = userInfo.headsetId
                if (businessId?.isBlank() == true) {
                    _headsetsScreenUiState.value =
                        EquipmentScreenUiState.OnFailedToLoadData("Invalid business ID. Please sign in again.")
                    return@collect
                }
                businessId?.let {
                    repo.getHeadsets(businessId, equipmentId).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                response.data?.let { headsetsData ->
                                    headsets = headsetsData
                                    headsets.find {
                                        it.serialNumber == userInfo.headsetId
                                    }?.let { headset ->
                                        selectedIndex = headsets.indexOf(headset)
                                    }
                                    _headsetsScreenUiState.value =
                                        EquipmentScreenUiState.OnDataRetrieved(headsets)
                                }
                            }

                            is Response.Error -> {
                                response.message?.let { errorMessage ->
                                    _headsetsScreenUiState.value =
                                        EquipmentScreenUiState.OnFailedToLoadData(errorMessage)
                                }
                            }

                            is Response.Loading -> {
                                _headsetsScreenUiState.value = EquipmentScreenUiState.OnLoading
                            }
                        }
                    }
                }
            }
        }
    }
}