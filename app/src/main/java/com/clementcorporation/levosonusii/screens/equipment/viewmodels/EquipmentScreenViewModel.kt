package com.clementcorporation.levosonusii.screens.equipment.viewmodels

import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.ELECTRIC_PALLET_JACK
import com.clementcorporation.levosonusii.main.Constants.FORKLIFT
import com.clementcorporation.levosonusii.main.Constants.HEADSET
import com.clementcorporation.levosonusii.main.Constants.MESSENGER_IDS
import com.clementcorporation.levosonusii.main.Constants.SCANNER
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.screens.equipment.model.Equipment
import com.clementcorporation.levosonusii.util.AuthenticationUtil
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CONNECTION = "CONNECTION"
private const val IS_AVAILABLE = "IS_AVAILABLE"
private const val TYPE = "TYPE"

@HiltViewModel
class EquipmentScreenViewModel @Inject constructor(
    private val resources: Resources,
    private val userInfo: DataStore<LSUserInfo>,
    private val voiceProfile: DataStore<VoiceProfile>
): ViewModel() {
    private val collection = FirebaseFirestore.getInstance().collection("HannafordFoods")
    private val document = collection.document(Constants.EQUIPMENT)
    private val _machineLiveData = MutableLiveData<List<Equipment>>()
    val machineLiveData: LiveData<List<Equipment>> get() = _machineLiveData
    private val _headsetLiveData = MutableLiveData<List<Equipment.Headset>>()
    val headsetLiveData: LiveData<List<Equipment.Headset>> get() = _headsetLiveData
    private val _scannerLiveData = MutableLiveData<List<Equipment.ProductScanner>>()
    val scannerLiveData: LiveData<List<Equipment.ProductScanner>> get() = _scannerLiveData
    val showProgressBar = mutableStateOf(true)
    val expandMenu = mutableStateOf(false)
    private val selectedMachineId = mutableStateOf("")
    private val selectedHeadsetId = mutableStateOf("")
    private val selectedScannerId = mutableStateOf("")

    init {
        viewModelScope.launch {
            userInfo.data.collect {
                val isForkliftOperator = it.operatorType == resources.getString(R.string.operator_type_forklift_tile_text)
                if (isForkliftOperator) retrieveForkliftsData() else retrieveElectricPalletJacksData()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            showProgressBar.value = true
            expandMenu.value = false
            AuthenticationUtil.signOut(userInfo, voiceProfile)
        }
    }

    private fun retrieveForkliftsData() {
        viewModelScope.launch {
            userInfo.data.collect { info ->
                showProgressBar.value = true
                document.get().addOnSuccessListener { forkliftDocument ->
                    val forklifts = arrayListOf<Equipment.Forklift>()
                    forkliftDocument.data?.forEach {
                        val id = it.key
                        var type = ""
                        val isSelected = mutableStateOf(false)
                        var isAvailable = false
                        val forkliftDetails = (it.value as Map<*, *>)
                        forkliftDetails.forEach { details ->
                            when (details.key) {
                                TYPE -> type = details.value as String
                                IS_AVAILABLE -> isAvailable = details.value as Boolean
                            }
                            isSelected.value = info.machineId == id
                        }
                        if ((type == FORKLIFT && isAvailable) || isSelected.value) forklifts.add(
                            Equipment.Forklift(
                                serialNumber = id,
                                isAvailable = isAvailable,
                                isSelected = isSelected
                            )
                        )
                    }
                    _machineLiveData.postValue(forklifts.toList())
                    showProgressBar.value = false
                }
            }
        }
    }
    private fun retrieveElectricPalletJacksData() {
        viewModelScope.launch {
            userInfo.data.collect { info ->
                showProgressBar.value = true
                document.get().addOnSuccessListener { epjDocument ->
                    val epjs = arrayListOf<Equipment.ElectricPalletJack>()
                    epjDocument.data?.forEach {
                        val id = it.key
                        var type = ""
                        val isSelected = mutableStateOf(false)
                        var isAvailable = false
                        val epjDetails = (it.value as Map<*, *>)
                        epjDetails.forEach { details ->
                            when (details.key) {
                                TYPE -> type = details.value as String
                                IS_AVAILABLE -> isAvailable = details.value as Boolean
                            }
                            isSelected.value = info.machineId == id
                            if (isSelected.value) setSelectedMachineId(id)
                        }
                        if ((type == ELECTRIC_PALLET_JACK && isAvailable) || isSelected.value) epjs.add(
                            Equipment.ElectricPalletJack(
                                serialNumber = id,
                                isAvailable = isAvailable,
                                isSelected = isSelected
                            )
                        )
                    }
                    _machineLiveData.postValue(epjs.toList())
                    showProgressBar.value = false
                }
            }
        }
    }

    fun retrieveHeadsetsData() {
        viewModelScope.launch {
            userInfo.data.collect { info ->
                showProgressBar.value = true
                document.get().addOnSuccessListener { headsetDocument ->
                    val headsets = arrayListOf<Equipment.Headset>()
                    headsetDocument.data?.forEach {
                        val id = it.key
                        var type = ""
                        var connection = ""
                        val isSelected = mutableStateOf(false)
                        var isAvailable = false
                        val headsetDetails = (it.value as Map<*, *>)
                        headsetDetails.forEach { details ->
                            when (details.key) {
                                TYPE -> type = details.value as String
                                CONNECTION -> connection = details.value as String
                                IS_AVAILABLE -> isAvailable = details.value as Boolean
                            }
                            isSelected.value = info.headsetId == id
                            if (isSelected.value) setSelectedHeadsetId(id)
                        }
                        if ((type == HEADSET && isAvailable) || isSelected.value) headsets.add(
                            Equipment.Headset(
                                serialNumber = id,
                                connection = connection,
                                isAvailable = isAvailable,
                                isSelected = isSelected
                            )
                        )
                        }
                    _headsetLiveData.postValue(headsets.toList())
                    showProgressBar.value = false
                    }
                }
        }
    }

    fun retrieveScannersData() {
        viewModelScope.launch {
            userInfo.data.collect { info ->
                showProgressBar.value = true
                document.get().addOnSuccessListener { scannerDocument ->
                    val scanners = arrayListOf<Equipment.ProductScanner>()
                    scannerDocument.data?.forEach {
                        val id = it.key
                        var type = ""
                        var connection = ""
                        val isSelected = mutableStateOf(false)
                        var isAvailable = false
                        val scannerDetails = (it.value as Map<*, *>)
                        scannerDetails.forEach { details ->
                            when (details.key) {
                                TYPE -> type = details.value as String
                                CONNECTION -> connection = details.value as String
                                IS_AVAILABLE -> isAvailable = details.value as Boolean
                            }
                            isSelected.value = info.scannerId == id
                            if (isSelected.value) setSelectedScannerId(id)
                        }
                        if ((type == SCANNER && isAvailable) || isSelected.value) {
                            scanners.add(
                                Equipment.ProductScanner(
                                    serialNumber = id,
                                    connection = connection,
                                    isAvailable = isAvailable,
                                    isSelected = isSelected
                                )
                            )
                        }
                    }
                    _scannerLiveData.postValue(scanners.toList())
                    showProgressBar.value = false
                }
            }
        }
    }

    fun setSelectedMachineId(equipmentId: String) {
        viewModelScope.launch {
            selectedMachineId.value = equipmentId
        }
    }

    fun setSelectedHeadsetId(equipmentId: String) {
        viewModelScope.launch {
            selectedHeadsetId.value = equipmentId
        }
    }

    fun setSelectedScannerId(equipmentId: String) {
        viewModelScope.launch {
            selectedScannerId.value = equipmentId
        }
    }

    fun updateMachinesData() {
        viewModelScope.launch {
            userInfo.data.collect { info ->
                showProgressBar.value = true
                document.get().addOnSuccessListener { machinesDocument ->
                    machinesDocument.data?.forEach {
                        val id = it.key
                        var type = ""
                        val machineDetails = (it.value as Map<*, *>)
                        machineDetails.forEach { details ->
                            when (details.key) {
                                TYPE -> type = details.value as String
                            }
                        }
                        if (type == FORKLIFT || type == ELECTRIC_PALLET_JACK) {
                            if (id == selectedMachineId.value) {
                                document.update(
                                    id,
                                    mapOf(
                                        TYPE to type,
                                        IS_AVAILABLE to false
                                    )
                                )
                            } else if (id == info.machineId) {
                                document.update(
                                    id,
                                    mapOf(
                                        TYPE to type,
                                        IS_AVAILABLE to true
                                    )
                                )
                            }
                        }
                    }
                }
                updateUserInfo()
                showProgressBar.value = false
            }
        }
    }

    fun updateHeadsetData() {
        viewModelScope.launch {
            userInfo.data.collect { info ->
                showProgressBar.value = true
                document.get().addOnSuccessListener { headsetDocument ->
                    headsetDocument.data?.forEach {
                        val id = it.key
                        var type = ""
                        var connection = ""
                        val headsetDetails = (it.value as Map<*, *>)
                        headsetDetails.forEach { details ->
                            when (details.key) {
                                TYPE -> type = details.value as String
                                CONNECTION -> connection = details.value as String
                            }
                        }
                        if (type == HEADSET) {
                            if (id == selectedHeadsetId.value) {
                                document.update(
                                    id,
                                    mapOf(
                                        TYPE to HEADSET,
                                        CONNECTION to connection,
                                        IS_AVAILABLE to false
                                    )
                                )
                            } else if (id == info.headsetId) {
                                document.update(
                                    id,
                                    mapOf(
                                        TYPE to HEADSET,
                                        CONNECTION to connection,
                                        IS_AVAILABLE to true
                                    )
                                )
                            }
                        }
                    }
                }
                updateUserInfo()
                showProgressBar.value = false
            }
        }
    }

    fun updateScannerData() {
        viewModelScope.launch {
            userInfo.data.collect { info ->
                showProgressBar.value = true
                document.get().addOnSuccessListener { scannerDocument ->
                    scannerDocument.data?.forEach {
                        val id = it.key
                        var type = ""
                        var connection = ""
                        val scannerDetails = (it.value as Map<*, *>)
                        scannerDetails.forEach { details ->
                            when (details.key) {
                                TYPE -> type = details.value as String
                                CONNECTION -> connection = details.value as String
                            }
                        }
                        if (type == SCANNER) {
                            if (id == selectedScannerId.value) {
                                document.update(
                                    id,
                                    mapOf(
                                        TYPE to SCANNER,
                                        CONNECTION to connection,
                                        IS_AVAILABLE to false
                                    )
                                )
                            } else if (id == info.scannerId) {
                                document.update(
                                    id,
                                    mapOf(
                                        TYPE to SCANNER,
                                        CONNECTION to connection,
                                        IS_AVAILABLE to true
                                    )
                                )
                            }
                        }
                    }
                }
                updateUserInfo()
                showProgressBar.value = false
            }
        }
    }

    private fun updateUserInfo() {
        viewModelScope.launch {
            userInfo.data.collect { info ->
                voiceProfile.data.collect { vpInfo ->
                    collection.document(Constants.USERS).update(
                        info.employeeId,
                        mapOf(
                            Constants.DEPARTMENT_ID to info.departmentId,
                            Constants.MACHINE_ID to selectedMachineId.value.ifEmpty { info.machineId },
                            Constants.HEADSET_ID to selectedHeadsetId.value.ifEmpty { info.headsetId },
                            Constants.SCANNER_ID to selectedScannerId.value.ifEmpty { info.scannerId },
                            Constants.NAME to info.name,
                            Constants.EMAIL to info.emailAddress,
                            Constants.PIC_URL to info.profilePicUrl,
                            Constants.USER_ID to info.firebaseId,
                            Constants.VOICE_PROFILE to vpInfo.voiceProfileMap,
                            Constants.OP_TYPE to info.operatorType,
                            MESSENGER_IDS to info.messengerIds
                        )
                    )
                    userInfo.updateData {
                        it.copy(
                            name = info.name,
                            employeeId = info.employeeId,
                            firebaseId = info.firebaseId,
                            departmentId = info.departmentId,
                            machineId = selectedMachineId.value.ifEmpty { info.machineId },
                            headsetId = selectedHeadsetId.value.ifEmpty { info.headsetId },
                            scannerId = selectedScannerId.value.ifEmpty { info.scannerId },
                            emailAddress = info.emailAddress,
                            profilePicUrl = info.profilePicUrl,
                            operatorType = info.operatorType,
                            messengerIds = info.messengerIds
                        )
                    }
                }
            }
        }
    }
}