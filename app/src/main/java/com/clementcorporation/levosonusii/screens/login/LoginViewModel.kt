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
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


//MOVE SIGN-IN FUNCTIONALITY TO MAIN VIEW MODEL

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
    ): ViewModel()
{
    private val auth: FirebaseAuth = Firebase.auth

    fun signInWithEmailAndPassword(context: Context, userId: String, password: String, home: () -> Unit = {}) {
        var name: String? = ""
        var email: String? = ""
        var profilePicUrl: String? = ""
        var firebaseId: String? = ""
        var voiceProfile: Map<*,*>? = hashMapOf<String, ArrayList<String>>()
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance().collection("HannafordFoods")
                    .document("users").get().addOnCompleteListener { document ->
                        document.result?.get(userId)?.let {
                            name = (it as HashMap<*,*>)["name"] as String
                            email = it["emailAddress"] as String
                            firebaseId = it["userId"] as String
                            profilePicUrl = it["profilePicUrl"] as String
                            voiceProfile = it["voiceProfile"] as HashMap<*, *>
                            email?.let { email ->
                                auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                    .addOnCompleteListener { task ->
                                        Log.d("Sign In: ", "SUCCESS")
                                        name?.let { name ->
                                            profilePicUrl?.let { url ->
                                                voiceProfile?.let { voiceProfile ->
                                                    firebaseId?.let { firebaseId ->
                                                        viewModelScope.launch {
                                                            sessionDataStore.updateData { userInfo ->
                                                                userInfo.copy(
                                                                    employeeId = userId,
                                                                    firebaseId = firebaseId,
                                                                    emailAddress = email,
                                                                    name = name,
                                                                    profilePicUrl = url
                                                                )
                                                            }
                                                            voiceProfileDataStore.updateData { vp ->
                                                                vp.copy(voiceProfileMap = voiceProfile as HashMap<String, ArrayList<String>>)
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
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    Log.d("Sign In: ", it)
                }
            }
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