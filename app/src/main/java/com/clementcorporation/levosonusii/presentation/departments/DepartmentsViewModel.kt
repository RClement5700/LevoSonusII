package com.clementcorporation.levosonusii.presentation.departments

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DepartmentsViewModel @Inject constructor(
    private val repo: DepartmentsRepository,
    private val signOutUseCase: SignOutUseCase,
    private val sessionDataStore: DataStore<LSUserInfo>
): ViewModel() {
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

    private fun fetchDepartmentsData() {
        viewModelScope.launch {
            sessionDataStore.data.collectLatest { userInfo ->
                val businessId = userInfo.organization.id
                repo.fetchDepartmentsData(businessId).collectLatest { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data?.let { departments ->
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
                }
            }
        }
    }

    fun updateUsersDepartment() {

    }

}

sealed class DepartmentsScreenUiState {
    data object Loading: DepartmentsScreenUiState()
    data class Error(val message: String): DepartmentsScreenUiState()
    data class DataRetrieved(val data: List<DepartmentUiModel>): DepartmentsScreenUiState()
}