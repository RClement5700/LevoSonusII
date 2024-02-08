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
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DepartmentsViewModel"
@HiltViewModel
class DepartmentsViewModel @Inject constructor(
    private val repo: DepartmentsRepository,
    private val signOutUseCase: SignOutUseCase,
    private val sessionDataStore: DataStore<LSUserInfo>
): ViewModel() {
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

    fun getCurrentDepartment(): DepartmentUiModel = departments[selectedIndex]

    fun updateUsersDepartment() {
        viewModelScope.launch {
            val userInfo = sessionDataStore.data.first()
            val currentDepartmentId = userInfo.departmentId
            val newDepartment = departments[selectedIndex]
            val newDepartmentId = newDepartment.id
            if (currentDepartmentId != newDepartmentId) {
                addOperatorToDepartment(newDepartmentId)
                removeOperatorFromDepartment()
            }
        }
    }

    private fun fetchDepartmentsData() {
        viewModelScope.launch {
            val userInfo = sessionDataStore.data.first()
            val response = repo.fetchDepartmentsData()
            if (response != null) {
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
                        response.message?.let {
                            _departmentsScreenEventsStateFlow.value =
                                DepartmentsScreenUiState.Error(it)
                        }
                    }
                    is Response.Loading -> {
                        _departmentsScreenEventsStateFlow.value =
                            DepartmentsScreenUiState.Loading
                    }
                }
            } else {
                _departmentsScreenEventsStateFlow.value =
                    DepartmentsScreenUiState.Error("Failed to load departments' data")
            }
        }
    }

    private fun addOperatorToDepartment(newDepartmentId: String) {
        viewModelScope.launch {
            val response = repo.addOperatorToDepartment(newDepartmentId)
            response?.data?.let { Log.d(TAG, it) }
        }
    }

    private fun removeOperatorFromDepartment() {
        viewModelScope.launch {
            val response = repo.subtractOperatorFromDepartment()
            response?.data?.let { Log.d(TAG, it) }
        }
    }
}

sealed class DepartmentsScreenUiState {
    data object Loading: DepartmentsScreenUiState()
    data class Error(val message: String): DepartmentsScreenUiState()
    data class DataRetrieved(val data: List<DepartmentUiModel>): DepartmentsScreenUiState()
}