package com.clementcorporation.levosonusii.presentation.register

import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.RegisterRepository
import com.clementcorporation.levosonusii.util.Constants.VALID_PASSWORD_LENGTH
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val EMAIL_VALIDATOR_AT = "@"
private const val EMAIL_VALIDATOR_COM = ".com"
sealed class RegisterScreenEvents {
    data object OnScreenCreated: RegisterScreenEvents()
    data class OnLoading(val isLoading: Boolean): RegisterScreenEvents()
    data class OnUserDataRetrieved(val user: LSUserInfo): RegisterScreenEvents()
    data class OnFailedToLoadUser(val message: String): RegisterScreenEvents()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: RegisterRepository,
    private val sessionDataStore: DataStore<LSUserInfo>,
): ViewModel() {
    val employeeId = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    private val _registerScreenEvents = MutableStateFlow<RegisterScreenEvents>(
        RegisterScreenEvents.OnScreenCreated
    )
    val registerScreenEvents get() = _registerScreenEvents.asStateFlow()
    
    fun validateInputs(): Boolean {
        return email.value.contains(EMAIL_VALIDATOR_AT) && email.value.contains(EMAIL_VALIDATOR_COM)
                && password.value.isNotEmpty() && password.value.isDigitsOnly()
                && password.value.length == VALID_PASSWORD_LENGTH
                && firstName.value.isNotEmpty() && lastName.value.isNotEmpty() && email.value.isNotEmpty()
    }

    fun createNewUser() {
        viewModelScope.launch {
            if (validateInputs())
                sessionDataStore.data.collectLatest { userInfo ->
                    repo.register(
                        userInfo.organization,
                        firstName.value,
                        lastName.value,
                        password.value,
                        email.value
                    ).collectLatest { response ->
                        when(response) {
                            //TODO: -Display user credentials to new user then prompt use to
                            //          open VoiceCommandTrainingWindow or ContinueToHomeScreen
                            //      -Write up the SignInUseCase
                            is Response.Success -> {
                                response.data?.let { newUser ->
                                    _registerScreenEvents.value =
                                        RegisterScreenEvents.OnUserDataRetrieved(newUser)
                                }
                            }
                            is Response.Loading -> {
                                _registerScreenEvents.value =
                                    RegisterScreenEvents.OnLoading(true)
                            }
                            is Response.Error -> {
                                response.message?.let { errorMessage ->
                                    _registerScreenEvents.value =
                                        RegisterScreenEvents.OnFailedToLoadUser(errorMessage)
                                }
                            }
                        }
                    }
                }
            else {
                _registerScreenEvents.value =
                    RegisterScreenEvents.OnFailedToLoadUser("Input Error...")
            }
        }
    }
}