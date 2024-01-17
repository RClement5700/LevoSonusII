package com.clementcorporation.levosonusii.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.RegisterRepository
import com.clementcorporation.levosonusii.util.Constants.VALID_PASSWORD_LENGTH
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterScreenUiState {
    data object OnScreenCreated: RegisterScreenUiState()
    data class OnLoading(val isLoading: Boolean): RegisterScreenUiState()
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
    private val sessionDataStore: DataStore<LSUserInfo>
): ViewModel() {
    var employeeId by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    private val _registerScreenUiState = MutableStateFlow<RegisterScreenUiState>(
        RegisterScreenUiState.OnScreenCreated
    )
    val registerScreenUiState get() = _registerScreenUiState.asStateFlow()
    
    fun validateInputs(): Boolean {
        return email.contains(EMAIL_VALIDATOR_AT) && email.contains(EMAIL_VALIDATOR_COM)
                && password.isNotEmpty() && password.isDigitsOnly()
                && password.length == VALID_PASSWORD_LENGTH
                && firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()
    }

    fun signIn(isCreatingVoiceProfile: Boolean) {
        viewModelScope.launch {
            _registerScreenUiState.value = RegisterScreenUiState.OnLoading(true)
            sessionDataStore.data.collectLatest { userInfo ->
                val businessId = userInfo.organization.id
                repo.signIn(businessId, employeeId.trim(), password.trim()).collectLatest {
                    response ->
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
                                RegisterScreenUiState.OnLoading(true)
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
            _registerScreenUiState.value = RegisterScreenUiState.OnLoading(false)
        }
    }

    fun createNewUser() {
        viewModelScope.launch {
            _registerScreenUiState.value = RegisterScreenUiState.OnLoading(true)
            if (validateInputs())
                sessionDataStore.data.collectLatest { userInfo ->
                    repo.register(
                        userInfo.organization,
                        firstName.trim(),
                        lastName.trim(),
                        password.trim(),
                        email.trim()
                    ).collectLatest { response ->
                        when(response) {
                            is Response.Success -> {
                                response.data?.let { newUser ->
                                    employeeId = newUser.employeeId
                                    _registerScreenUiState.value =
                                        RegisterScreenUiState.OnUserDataRetrieved(newUser)
                                    cancel()
                                }
                            }
                            is Response.Loading -> {
                                _registerScreenUiState.value =
                                    RegisterScreenUiState.OnLoading(true)
                            }
                            is Response.Error -> {
                                response.message?.let { errorMessage ->
                                    _registerScreenUiState.value =
                                        RegisterScreenUiState.OnFailedToLoadUser(errorMessage)
                                }
                            }
                        }
                    }
                }
            else {
                _registerScreenUiState.value =
                    RegisterScreenUiState.OnFailedToLoadUser("Input Error...")
            }
            _registerScreenUiState.value = RegisterScreenUiState.OnLoading(false)
        }
    }
}