package com.clementcorporation.levosonusii.presentation.departments

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.Department
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.VoiceProfile
import com.clementcorporation.levosonusii.domain.repositories.DepartmentsRepository
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.util.Constants.DEPARTMENT_ID
import com.clementcorporation.levosonusii.util.Constants.EMAIL
import com.clementcorporation.levosonusii.util.Constants.HEADSET_ID
import com.clementcorporation.levosonusii.util.Constants.MACHINE_ID
import com.clementcorporation.levosonusii.util.Constants.MESSENGER_IDS
import com.clementcorporation.levosonusii.util.Constants.NAME
import com.clementcorporation.levosonusii.util.Constants.OP_TYPE
import com.clementcorporation.levosonusii.util.Constants.ORGANIZATION_ID
import com.clementcorporation.levosonusii.util.Constants.PIC_URL
import com.clementcorporation.levosonusii.util.Constants.SCANNER_ID
import com.clementcorporation.levosonusii.util.Constants.USERS
import com.clementcorporation.levosonusii.util.Constants.USER_ID
import com.clementcorporation.levosonusii.util.Constants.VOICE_PROFILE
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DepartmentsViewModel @Inject constructor(
    private val repo: DepartmentsRepository,
    private val signOutUseCase: SignOutUseCase,
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>,
): ViewModel() {
    private val _departmentsScreenEventsStateFlow = MutableStateFlow<DepartmentsScreenUiState>(
        DepartmentsScreenUiState.Loading
    )
    val departmentsScreenEventsStateFlow = _departmentsScreenEventsStateFlow.asStateFlow()
    private val selectedDepartmentId = mutableStateOf("")
    val showProgressBar = mutableStateOf(true)
    val expandMenu = mutableStateOf(false)

    init {
        fetchDepartmentsData()
    }

    private fun fetchDepartmentsData() {
        viewModelScope.launch {
            sessionDataStore.data.collectLatest { userInfo ->
                repo.fetchDepartmentsData(userInfo).collectLatest { result ->
                    if (result is Response.Success) {
                        result.data?.let { departmentsData ->
                            selectedDepartmentId.value =
                                departmentsData.find { it.isSelected.value }?.id.toString()
                            _departmentsScreenEventsStateFlow.emit(
                                DepartmentsScreenUiState.DataRetrieved(
                                    departmentsData
                                )
                            )
                        }
                    } else result.message?.let {
                        _departmentsScreenEventsStateFlow.emit(
                            DepartmentsScreenUiState.Error
                        )
                    }
                }
            }
        }
    }

    private fun subtractOrderPickerFromDepartment() {
        viewModelScope.launch {
            sessionDataStore.data.collectLatest { userInfo ->
                repo.subtractOrderPickerFromDepartment(userInfo)
            }
        }
    }

    private fun addOrderPickerToDepartment() {
        if (selectedDepartmentId.value.isNotEmpty()) {
            viewModelScope.launch {
                sessionDataStore.data.collectLatest { userInfo ->
                    repo.addOrderPickerToDepartment(userInfo, selectedDepartmentId.value)
                }
            }
        }
    }

    private fun updateUserInfo() {
        //TODO: when the second call to sessionDataStore is called first the selected department updates
        //  as expected but the necessary arithmetic is omitted. Why?
        viewModelScope.launch {
            sessionDataStore.updateData { userInfo ->
                userInfo.copy(
                    organization = userInfo.organization,
                    name = userInfo.name,
                    employeeId = userInfo.employeeId,
                    firebaseId = userInfo.firebaseId,
                    departmentId = selectedDepartmentId.value,
                    machineId = userInfo.machineId,
                    headsetId = userInfo.headsetId,
                    scannerId = userInfo.scannerId,
                    emailAddress = userInfo.emailAddress,
                    profilePicUrl = userInfo.profilePicUrl,
                    operatorType = userInfo.operatorType,
                    messengerIds = userInfo.messengerIds
                )
            }.also { userInfo ->
                voiceProfileDataStore.data.collectLatest { voiceProfile ->
                    val collection = FirebaseFirestore.getInstance().collection(userInfo.organization.name)
                    collection.document(USERS).update(
                        userInfo.employeeId,
                        mapOf(
                            ORGANIZATION_ID to userInfo.organization.id,
                            DEPARTMENT_ID to selectedDepartmentId.value,
                            MACHINE_ID to userInfo.machineId,
                            HEADSET_ID to userInfo.headsetId,
                            SCANNER_ID to userInfo.scannerId,
                            NAME to userInfo.name,
                            EMAIL to userInfo.emailAddress,
                            PIC_URL to userInfo.profilePicUrl,
                            USER_ID to userInfo.firebaseId,
                            VOICE_PROFILE to voiceProfile.voiceProfileMap,
                            OP_TYPE to userInfo.operatorType,
                            MESSENGER_IDS to userInfo.messengerIds
                        )
                    )
                }
            }
        }
    }

    fun setSelectedDepartment(departmentId: String) {
        selectedDepartmentId.value = departmentId
    }

    fun updateUserDepartment() {
        showProgressBar.value = true
        updateUserInfo()
        addOrderPickerToDepartment()
        subtractOrderPickerFromDepartment()
        showProgressBar.value = false
    }

    fun signOut() {
        viewModelScope.launch {
            showProgressBar.value = true
            expandMenu.value = false
            signOutUseCase.invoke()
        }
    }
}

sealed class DepartmentsScreenUiState {
    data object Loading: DepartmentsScreenUiState()
    data object Error: DepartmentsScreenUiState()
    class DataRetrieved(val data: List<Department>): DepartmentsScreenUiState()
}