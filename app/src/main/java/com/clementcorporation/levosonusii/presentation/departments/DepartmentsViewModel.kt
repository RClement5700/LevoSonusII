package com.clementcorporation.levosonusii.presentation.departments

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.DepartmentUiModel
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.DepartmentsRepository
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.util.OperatorTypes
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DepartmentsViewModel"
@HiltViewModel
class DepartmentsViewModel @Inject constructor(
    private val repo: DepartmentsRepository,
    private val signOutUseCase: SignOutUseCase,
    private val sessionDataStore: DataStore<LSUserInfo>
): ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var departments: List<DepartmentUiModel>
    private val _departmentsScreenEventsStateFlow = MutableStateFlow<DepartmentsScreenUiState>(
        DepartmentsScreenUiState.Loading
    )
    val departmentsScreenEventsStateFlow = _departmentsScreenEventsStateFlow.asStateFlow()
    var selectedIndex by mutableIntStateOf(-1)
    val showProgressBar = mutableStateOf(true)
    val expandMenu = mutableStateOf(false)

    init {
        fetchDepartmentsData()
    }

    fun signOut(navigate: () -> Unit) {
        viewModelScope.launch {
            showProgressBar.value = true
            expandMenu.value = false
            signOutUseCase.invoke(navigate)
        }
    }

    fun getSessionDataStore() = sessionDataStore

    fun getCurrentDepartment(): DepartmentUiModel = departments[selectedIndex]

    fun updateUsersDepartment() {
        scope.launch {
            sessionDataStore.data.collect { userInfo ->
                val currentDepartmentId = userInfo.departmentId
                val newDepartment = departments[selectedIndex]
                val newDepartmentId = newDepartment.id
                if (currentDepartmentId != newDepartmentId) {
                    addOperatorToDepartment(newDepartmentId, userInfo)
                    removeOperatorFromDepartment(userInfo)
                }
            }
        }
    }

    fun fetchDepartmentsData() {
        scope.launch {
            sessionDataStore.data.collect { userInfo ->
                val businessId = userInfo.organization?.id
                if (businessId?.isBlank() == true) {
                    _departmentsScreenEventsStateFlow.value =
                        DepartmentsScreenUiState.Error("Invalid business ID. Please sign in again.")
                    return@collect
                }
                businessId?.let {
                    repo.fetchDepartmentsData(businessId).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                response.data?.let { departmentsData ->
                                    departments = departmentsData
                                    departments.find {
                                        it.id == userInfo.departmentId
                                    }?.let { department ->
                                        selectedIndex = departments.indexOf(department)
                                    }
                                    _departmentsScreenEventsStateFlow.value =
                                        DepartmentsScreenUiState.DataRetrieved(departments)
                                }
                            }

                            is Response.Error -> {
                                response.message?.let { errorMessage ->
                                    _departmentsScreenEventsStateFlow.value =
                                        DepartmentsScreenUiState.Error(errorMessage)
                                }
                            }

                            is Response.Loading -> {
                                _departmentsScreenEventsStateFlow.value =
                                    DepartmentsScreenUiState.Loading
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addOperatorToDepartment(newDepartmentId: String, userInfo: LSUserInfo) {
        scope.launch {
            val businessId = userInfo.organization?.id
            val isOrderPicker = userInfo.operatorType == OperatorTypes.ORDER_PICKER
            repo.addOperatorToDepartment(
                newDepartmentId,
                businessId.orEmpty(),
                isOrderPicker
            ).collect { response ->
                response.data?.let {
                    Log.d(TAG, it)
                }
            }
        }
    }

    private fun removeOperatorFromDepartment(userInfo: LSUserInfo) {
        scope.launch {
            val businessId = userInfo.organization?.id
            val isOrderPicker = userInfo.operatorType == OperatorTypes.ORDER_PICKER
            val departmentId = userInfo.departmentId
            repo.subtractOperatorFromDepartment(
                departmentId,
                businessId.orEmpty(),
                isOrderPicker
            ).collect { response ->
                response.data?.let {
                    Log.d(TAG, it)
                    _departmentsScreenEventsStateFlow.value = DepartmentsScreenUiState.OnDataUpdated(response)
                }
            }
        }
    }
}

sealed class DepartmentsScreenUiState {
    data object Loading: DepartmentsScreenUiState()
    data class Error(val message: String): DepartmentsScreenUiState()
    data class DataRetrieved(val data: List<DepartmentUiModel>): DepartmentsScreenUiState()
    data class OnDataUpdated(val response: Response<String>): DepartmentsScreenUiState()
}