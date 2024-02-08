package com.clementcorporation.levosonusii.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.LoginRepository
import com.clementcorporation.levosonusii.domain.use_cases.GetBusinessesUseCase
import com.clementcorporation.levosonusii.util.Constants.VALID_BUSINESS_ID_LENGTH
import com.clementcorporation.levosonusii.util.Constants.VALID_EMPLOYEE_ID_LENGTH
import com.clementcorporation.levosonusii.util.Constants.VALID_PASSWORD_LENGTH
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginScreenUiState {
    data object OnLoading: LoginScreenUiState()
    data object OnBusinessesRetrieved: LoginScreenUiState()
    data class OnUserDataRetrieved(val user: LSUserInfo): LoginScreenUiState()
    data class OnFailedToLoadUser(val message: String): LoginScreenUiState()
    data object OnFailedToLoadBusinesses: LoginScreenUiState()
}
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: LoginRepository,
    private val getBusinessesUseCase: GetBusinessesUseCase,
    private val sessionDataStore: DataStore<LSUserInfo>
): ViewModel() {
    var isVerifiedEmployeeId by mutableStateOf(false)
    var employeeId by mutableStateOf("")
    var password by mutableStateOf("")
    private lateinit var businesses: List<Business?>
    private val ioJob = CoroutineScope(Job() + Dispatchers.IO)
    private var searchJob: Job? = null
    private val _loginScreenUiState = MutableStateFlow<LoginScreenUiState>(
        LoginScreenUiState.OnLoading
    )
    val loginScreenUiState = _loginScreenUiState.asStateFlow()

    init {
        fetchBusinesses()
    }

    fun fetchBusinesses() {
        ioJob.launch {
            getBusinessesUseCase.invoke().collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        response.data?.let { list ->
                            businesses = list
                        }
                        _loginScreenUiState.value = LoginScreenUiState.OnBusinessesRetrieved
                    }
                    is Response.Error -> {
                        _loginScreenUiState.value = LoginScreenUiState.OnFailedToLoadBusinesses
                    }
                    else -> {
                        _loginScreenUiState.value = LoginScreenUiState.OnLoading
                    }
                }
            }
        }
    }

    private fun filterBusinesses(): Business? =
        businesses.find { it?.id == employeeId.substring(0, VALID_BUSINESS_ID_LENGTH) }

    fun onBusinessRetrieved() {
        ioJob.cancel()
    }
    fun onQueryChange() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            val businessMatch = filterBusinesses()
            isVerifiedEmployeeId = businessMatch != null
        }
    }

    fun validateInputs(): Boolean =
        employeeId.length >= VALID_EMPLOYEE_ID_LENGTH
                && password.length == VALID_PASSWORD_LENGTH
                && isVerifiedEmployeeId

    fun signIn() {
        viewModelScope.launch {
            sessionDataStore.data.collectLatest {
                val businessId = it.organization.id
                repo.signIn(businessId, employeeId, password).collectLatest { response ->
                    when(response) {
                        is Response.Success -> {
                            response.data?.let { user ->
                                _loginScreenUiState.value =
                                    LoginScreenUiState.OnUserDataRetrieved(user)
                                sessionDataStore.updateData {
                                    it.copy(
                                        organization = it.organization,
                                        name = user.name,
                                        emailAddress = user.emailAddress,
                                        employeeId = user.employeeId,
                                        firebaseId = user.firebaseId,
                                        profilePicUrl = user.profilePicUrl,
                                        headsetId = user.headsetId,
                                        machineId = user.machineId,
                                        departmentId = user.departmentId,
                                        scannerId = user.scannerId,
                                        operatorType = user.operatorType,
                                        messengerIds = user.messengerIds,
                                        voiceProfile = user.voiceProfile
                                    )
                                }
                            }
                        }
                        is Response.Error -> {
                            response.message?.let { errorMessage ->
                                _loginScreenUiState.value =
                                    LoginScreenUiState.OnFailedToLoadUser(errorMessage)
                            }
                        }
                        is Response.Loading -> {
                            _loginScreenUiState.value = LoginScreenUiState.OnLoading
                        }
                    }
                }
            }
        }
    }
}