package com.clementcorporation.levosonusii.presentation.login

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.LoginRepository
import com.clementcorporation.levosonusii.util.Constants.VALID_EMPLOYEE_ID_LENGTH
import com.clementcorporation.levosonusii.util.Constants.VALID_PASSWORD_LENGTH
import com.clementcorporation.levosonusii.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginScreenUiState {
    data object OnScreenCreated: LoginScreenUiState()
    data class OnLoading(val isLoading: Boolean): LoginScreenUiState()
    data class OnUserDataRetrieved(val user: LSUserInfo): LoginScreenUiState()
    data class OnFailedToLoadUser(val message: String): LoginScreenUiState()
}
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: LoginRepository,
    private val sessionDataStore: DataStore<LSUserInfo>
): ViewModel() {
    var employeeId by mutableStateOf("")
    var password by mutableStateOf("")
    private val _loginScreenUiState = MutableStateFlow<LoginScreenUiState>(
        LoginScreenUiState.OnScreenCreated
    )
    val loginScreenUiState = _loginScreenUiState.asStateFlow()

    fun validateInputs(): Boolean =
        employeeId.length >= VALID_EMPLOYEE_ID_LENGTH && password.length == VALID_PASSWORD_LENGTH

    fun signIn() {
        viewModelScope.launch {
            _loginScreenUiState.value = LoginScreenUiState.OnLoading(true)
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
                            _loginScreenUiState.value = LoginScreenUiState.OnLoading(true)
                        }
                    }
                }
            }
            _loginScreenUiState.value = LoginScreenUiState.OnLoading(false)
        }
    }
}