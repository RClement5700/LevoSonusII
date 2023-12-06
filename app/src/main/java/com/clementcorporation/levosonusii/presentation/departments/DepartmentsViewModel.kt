package com.clementcorporation.levosonusii.presentation.departments

import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.data.remote.DepartmentsRepositoryImpl
import com.clementcorporation.levosonusii.util.AuthenticationUtil
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
import com.clementcorporation.levosonusii.util.LSUserInfo
import com.clementcorporation.levosonusii.util.Resource
import com.clementcorporation.levosonusii.util.VoiceProfile
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DepartmentsViewModel @Inject constructor(
    private val resources: Resources,
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>,
): ViewModel() {
    private val repo = DepartmentsRepositoryImpl(resources, sessionDataStore)
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
            repo.fetchDepartmentsData().collectLatest { result ->
                if (result is Resource.Success) {
                    result.data?.let { departmentsData ->
                        selectedDepartmentId.value = departmentsData.find { it.isSelected.value }?.id.toString()
                        _departmentsScreenEventsStateFlow.emit(
                            DepartmentsScreenUiState.DataRetrieved(
                                departmentsData
                            )
                    ) }
                } else result.message?.let {
                    _departmentsScreenEventsStateFlow.emit(
                        DepartmentsScreenUiState.Error
                    )
                }
            }
        }
    }

    private fun subtractOrderPickerFromDepartment() {
        viewModelScope.launch {
            repo.subtractOrderPickerFromDepartment()
        }
    }

    private fun addOrderPickerToDepartment() {
        if (selectedDepartmentId.value.isNotEmpty()) {
            viewModelScope.launch {
                repo.addOrderPickerToDepartment(selectedDepartmentId.value)
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
            AuthenticationUtil.signOut(sessionDataStore, voiceProfileDataStore)
        }
    }
}

sealed class DepartmentsScreenUiState {
    object Loading: DepartmentsScreenUiState()
    object Error: DepartmentsScreenUiState()
    class DataRetrieved(val data: List<com.clementcorporation.levosonusii.presentation.departments.Department>): DepartmentsScreenUiState()
}