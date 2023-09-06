package com.clementcorporation.levosonusii.screens.login

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.main.Constants.DEPARTMENT_ID
import com.clementcorporation.levosonusii.main.Constants.EMAIL
import com.clementcorporation.levosonusii.main.Constants.HEADSET_ID
import com.clementcorporation.levosonusii.main.Constants.MACHINE_ID
import com.clementcorporation.levosonusii.main.Constants.MESSENGER_IDS
import com.clementcorporation.levosonusii.main.Constants.NAME
import com.clementcorporation.levosonusii.main.Constants.OP_TYPE
import com.clementcorporation.levosonusii.main.Constants.PIC_URL
import com.clementcorporation.levosonusii.main.Constants.SCANNER_ID
import com.clementcorporation.levosonusii.main.Constants.USERS
import com.clementcorporation.levosonusii.main.Constants.USER_ID
import com.clementcorporation.levosonusii.main.Constants.VOICE_PROFILE
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
): ViewModel() {
    val employeeId = mutableStateOf("")
    val password = mutableStateOf("")
    val isLoginButtonEnabled = mutableStateOf(false)
    val showProgressBar = mutableStateOf(false)

    fun signInWithEmailAndPassword(home: () -> Unit = {}) {
        var name: String? = ""
        var email: String? = ""
        var profilePicUrl: String? = ""
        var firebaseId: String? = ""
        var departmentId: String? = ""
        var machineId: String? = ""
        var scannerId: String? = ""
        var headsetId: String? = ""
        var operatorType: String? = ""
        var messengerIds: ArrayList<String>? = arrayListOf<String>()
        var voiceProfile: Map<*,*>? = hashMapOf<String, ArrayList<String>>()
        viewModelScope.launch {
            showProgressBar.value = true
            try {
                FirebaseFirestore.getInstance().collection("HannafordFoods")
                    .document(USERS).get().addOnCompleteListener { document ->
                        val userId = employeeId.value
                        document.result?.get(userId)?.let {
                            name = (it as HashMap<*,*>)[NAME] as String
                            email = it[EMAIL] as String
                            firebaseId = it[USER_ID] as String
                            departmentId = it[DEPARTMENT_ID] as String
                            machineId = it[MACHINE_ID] as String
                            scannerId = it[SCANNER_ID] as String
                            headsetId = it[HEADSET_ID] as String
                            operatorType = it[OP_TYPE] as String
                            profilePicUrl = it[PIC_URL] as String
                            messengerIds = it[MESSENGER_IDS] as ArrayList<String>
                            voiceProfile = it[VOICE_PROFILE] as HashMap<*, *>
                            email?.let { email ->
                                Firebase.auth.signInWithEmailAndPassword(email.trim(), password.value.trim())
                                    .addOnCompleteListener { task ->
                                        Log.d("Sign In: ", "SUCCESS")
                                        name?.let { name ->
                                            profilePicUrl?.let { url ->
                                                voiceProfile?.let { voiceProfile ->
                                                    firebaseId?.let { firebaseId ->
                                                        departmentId?.let { departmentId ->
                                                            machineId?.let { machineId ->
                                                                scannerId?.let { scannerId ->
                                                                    headsetId?.let { headsetId ->
                                                                        operatorType?.let { operatorType ->
                                                                            messengerIds?.let { messengerIds ->
                                                                                viewModelScope.launch {
                                                                                    sessionDataStore.updateData { userInfo ->
                                                                                        userInfo.copy(
                                                                                            employeeId = userId,
                                                                                            firebaseId = firebaseId,
                                                                                            emailAddress = email,
                                                                                            name = name,
                                                                                            profilePicUrl = url,
                                                                                            departmentId = departmentId,
                                                                                            machineId = machineId,
                                                                                            scannerId = scannerId,
                                                                                            headsetId = headsetId,
                                                                                            operatorType = operatorType,
                                                                                            messengerIds = messengerIds
                                                                                        )
                                                                                    }
                                                                                    voiceProfileDataStore.updateData { vp ->
                                                                                        vp.copy(
                                                                                            voiceProfileMap = voiceProfile as HashMap<String, ArrayList<String>>
                                                                                        )
                                                                                    }
                                                                                    home()
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                            }
                        }
                    }
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    Log.d("Sign In: ", it)
                }
            }
            showProgressBar.value = false
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