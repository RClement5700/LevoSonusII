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
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginScreenEvents {
    data object OnScreenCreated: LoginScreenEvents()
    data class OnLoading(val isLoading: Boolean): LoginScreenEvents()
    data class OnUserDataRetrieved(val user: LSUserInfo): LoginScreenEvents()
    data class OnFailedToLoadUser(val message: String): LoginScreenEvents()
}
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: LoginRepository,
    private val sessionDataStore: DataStore<LSUserInfo>
): ViewModel() {
    val employeeId = mutableStateOf("")
    val password = mutableStateOf("")
    private val _loginScreenEvents = MutableStateFlow<LoginScreenEvents>(
        LoginScreenEvents.OnScreenCreated
    )
    val loginScreenEvents get() = _loginScreenEvents.asStateFlow()

    fun validateInputs(): Boolean =
        employeeId.value.length >= VALID_EMPLOYEE_ID_LENGTH && password.value.length == VALID_PASSWORD_LENGTH

    fun signIn() {
        viewModelScope.launch {
            _loginScreenEvents.value = LoginScreenEvents.OnLoading(true)
            sessionDataStore.data.collectLatest {
                val businessId = it.organization.id
                repo.signIn(businessId, employeeId.value, password.value).collectLatest { response ->
                    when(response) {
                        is Response.Success -> {
                            response.data?.let { user ->
                                _loginScreenEvents.value =
                                    LoginScreenEvents.OnUserDataRetrieved(user)
                                cancel()
                            }
                        }
                        is Response.Error -> {
                            response.message?.let { errorMessage ->
                                _loginScreenEvents.value =
                                    LoginScreenEvents.OnFailedToLoadUser(errorMessage)
                            }
                        }
                        is Response.Loading -> {
                            _loginScreenEvents.value = LoginScreenEvents.OnLoading(true)
                        }
                    }
                }
            }
            _loginScreenEvents.value = LoginScreenEvents.OnLoading(false)
        }
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device: BluetoothDevice = result.device
            // ...do whatever you want with this found device
        }

        override fun onBatchScanResults(results: List<ScanResult?>?) {
            // Ignore for now
        }

        override fun onScanFailed(errorCode: Int) {
            // Ignore for now
        }
    }

    //scanMode = when and how long the Bluetooth stack is actually searching for devices
    private var scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
        .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
        .setReportDelay(0L)
        .build()

    fun scan(context: Context) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter: BluetoothAdapter = bluetoothManager.adapter
        val scanner: BluetoothLeScanner = adapter.bluetoothLeScanner
        val peripheralAddresses = arrayOf("01:0A:5C:7D:D0:1A")
        val filters: MutableList<ScanFilter?> = ArrayList()
        for (address in peripheralAddresses) {
            val filter = ScanFilter.Builder()
                .setDeviceAddress(address)
                .build()
            filters.add(filter)
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            scanner.startScan(filters, scanSettings, scanCallback)
            return
        }
        Log.d(TAG, "scan started");
    }
}