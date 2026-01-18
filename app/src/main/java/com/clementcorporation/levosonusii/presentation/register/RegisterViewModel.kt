package com.clementcorporation.levosonusii.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.RegisterRepository
import com.clementcorporation.levosonusii.domain.use_cases.GetBusinessesUseCase
import com.clementcorporation.levosonusii.util.Constants.VALID_BUSINESS_ID_LENGTH
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

sealed class RegisterScreenUiState {
    data object OnBusinessesRetrieved: RegisterScreenUiState()
    data object OnFailedToLoadBusinesses: RegisterScreenUiState()
    data object OnLoading: RegisterScreenUiState()
    data class OnUserDataRetrieved(val user: LSUserInfo): RegisterScreenUiState()
    data class OnFailedToLoadUser(val message: String): RegisterScreenUiState()
    data class OnSignInSuccess(val user: LSUserInfo, val isCreatingVoiceProfile: Boolean):RegisterScreenUiState()
    data class OnSignInFailure(val message: String): RegisterScreenUiState()
}

private const val EMAIL_VALIDATOR_AT = "@"
private const val EMAIL_VALIDATOR_COM = ".com"

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: RegisterRepository,
    private val getBusinessesUseCase: GetBusinessesUseCase,
): ViewModel() {
    var employeeId by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var businessId by mutableStateOf("")
    var isVerifiedBusinessId by mutableStateOf(false)
    private var searchJob: Job? = null
    private val ioJob = CoroutineScope(Job() + Dispatchers.IO)
    private val _registerScreenUiState = MutableStateFlow<RegisterScreenUiState>(
        RegisterScreenUiState.OnLoading
    )
    private lateinit var businesses: List<Business?>
    val registerScreenUiState = _registerScreenUiState.asStateFlow()

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
                        _registerScreenUiState.value = RegisterScreenUiState.OnBusinessesRetrieved
                    }
                    is Response.Error -> {
                        _registerScreenUiState.value = RegisterScreenUiState.OnFailedToLoadBusinesses
                    }
                    else -> {
                        _registerScreenUiState.value = RegisterScreenUiState.OnLoading
                    }
                }
            }
        }
    }

    private fun filterBusinesses(): Business? =
        businesses.find { it?.id == businessId }

    fun onNewUserCreated() {
        ioJob.cancel()
    }

    fun onQueryChange() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            val businessMatch = filterBusinesses()
            isVerifiedBusinessId = businessMatch != null
        }
    }
    
    fun validateInputs(): Boolean {
        return email.contains(EMAIL_VALIDATOR_AT) && email.contains(EMAIL_VALIDATOR_COM)
                && password.isNotEmpty() && password.isDigitsOnly()
                && password.length == VALID_PASSWORD_LENGTH
                && firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()
                && businessId.length == VALID_BUSINESS_ID_LENGTH && isVerifiedBusinessId
    }

    fun signIn(isCreatingVoiceProfile: Boolean) {
        viewModelScope.launch {
            //TODO: mimick invoke call from LoginViewModel; need authenticateUseCase
            repo.signIn(businessId, employeeId.trim()).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        response.data?.let { user ->
                            _registerScreenUiState.value =
                                RegisterScreenUiState.OnSignInSuccess(user, isCreatingVoiceProfile)
                            cancel()
                        }
                    }
                    is Response.Loading -> {
                        _registerScreenUiState.value =
                            RegisterScreenUiState.OnLoading
                    }
                    is Response.Error -> {
                        response.message?.let { errorMessage ->
                            _registerScreenUiState.value =
                                RegisterScreenUiState.OnSignInFailure(errorMessage)
                        }
                    }
                }
            }
        }
    }

    fun createNewUser() {
        ioJob.launch {
            if (validateInputs()) {
                repo.register(
                    businessId.trim(),
                    firstName.trim(),
                    lastName.trim(),
                    password.trim(),
                    email.trim()
                ).collectLatest { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data?.let { newUser ->
                                employeeId = newUser.employeeId
                                _registerScreenUiState.value =
                                    RegisterScreenUiState.OnUserDataRetrieved(newUser)
                            }
                        }

                        is Response.Loading -> {
                            _registerScreenUiState.value =
                                RegisterScreenUiState.OnLoading
                        }

                        is Response.Error -> {
                            response.message?.let { errorMessage ->
                                _registerScreenUiState.value =
                                    RegisterScreenUiState.OnFailedToLoadUser(errorMessage)
                            }
                        }
                    }
                }
            } else {
                _registerScreenUiState.value =
                    RegisterScreenUiState.OnFailedToLoadUser("Input Error...")
            }
        }
    }
}