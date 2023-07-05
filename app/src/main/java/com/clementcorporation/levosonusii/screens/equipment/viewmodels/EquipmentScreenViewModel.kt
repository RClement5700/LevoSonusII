package com.clementcorporation.levosonusii.screens.equipment.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.ELECTRIC_PALLET_JACKS
import com.clementcorporation.levosonusii.main.Constants.FORKLIFTS
import com.clementcorporation.levosonusii.main.Constants.HEADSETS
import com.clementcorporation.levosonusii.main.Constants.SCANNERS
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.screens.equipment.model.Equipment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

private const val ID = "id"
private const val IS_AVAIABLE = "isAvailable"
private const val TYPE = "type"

class EquipmentScreenViewModel: ViewModel() {
    private val collection = FirebaseFirestore.getInstance().collection("HannafordFoods")
    private val document = collection.document(Constants.EQUIPMENT)
    private val _equipmentLiveData = MutableLiveData<List<Equipment>>()
    val equipmentLiveData: LiveData<List<Equipment>> get() = _equipmentLiveData
    private val _currentEquipmentIdLiveData = MutableLiveData<String>()
    val currentEquipmentIdLiveData: LiveData<String> get() = _currentEquipmentIdLiveData
    val showProgressBar = mutableStateOf(true)
    private val selectedEquipmentId = mutableStateOf("")

    fun fetchCurrentEquipmentId(userInfo: LSUserInfo) {
        viewModelScope.launch {
            collection.document(Constants.USERS).get().addOnSuccessListener { task ->
                task.data?.forEach {
                    if (it.key == userInfo.employeeId) {
                        val userDetails = it.value as Map<*, *>
                        userDetails.forEach { details ->
                            when (details.key) {
                                Constants.EQUIPMENT_ID -> _currentEquipmentIdLiveData.postValue(details.value as String)
                            }
                        }
                    }
                }
            }
        }
    }

    fun retrieveForkliftsData(userInfo: LSUserInfo, isForkliftOperator: Boolean) {
        viewModelScope.launch {
            showProgressBar.value = true
            var isAvailable = false
            var id = ""
            val forkliftList = arrayListOf<Equipment.Forklift>()
            document.get().addOnSuccessListener { document ->
                val forklifts = document.get(FORKLIFTS) as List<Map<*,*>>
                forklifts.forEach { forklift ->
                    id = forklift[ID] as String
                    isAvailable = forklift[IS_AVAIABLE] as Boolean
                    if (isAvailable) forkliftList.add(
                        Equipment.Forklift(
                            id,
                            isAvailable,
                            mutableStateOf(userInfo.equipmentId == id && isForkliftOperator)
                        )
                    )
                }
                _equipmentLiveData.postValue(forkliftList)
            }
            showProgressBar.value = false
        }
    }
    fun retrieveElectricPalletJacksData(userInfo: LSUserInfo, isForkliftOperator: Boolean) {
        viewModelScope.launch {
            showProgressBar.value = true
            var isAvailable = false
            var id = ""
            val electricPalletJackList = arrayListOf<Equipment.ElectricPalletJack>()
            document.get().addOnSuccessListener { document ->
                val epjs = document.get(ELECTRIC_PALLET_JACKS) as List<Map<*,*>>
                epjs.forEach { epj ->
                    id = epj[ID] as String
                    isAvailable = epj[IS_AVAIABLE] as Boolean
                    if (isAvailable) electricPalletJackList.add(
                        Equipment.ElectricPalletJack(
                            id,
                            isAvailable,
                            mutableStateOf(userInfo.equipmentId == id && !isForkliftOperator)
                        )
                    )
                }
                _equipmentLiveData.postValue(electricPalletJackList)
            }
            showProgressBar.value = false
        }
    }

    private fun retrieveHeadsetsData() {
        viewModelScope.launch {
            var type = ""
            var isAvailable = false
            var id = ""
            val headsetList = arrayListOf<Equipment.Headset>()
            document.get().addOnSuccessListener { document ->
                val headsets = document.get(HEADSETS) as List<Map<*,*>>
                headsets.forEach { headset ->
                    type = headset[TYPE] as String
                    id = headset[ID] as String
                    isAvailable = headset[IS_AVAIABLE] as Boolean
                    if (isAvailable) headsetList.add(Equipment.Headset(type, id, isAvailable))
                }
                _equipmentLiveData.postValue(headsetList)
            }
        }
    }

    private fun retrieveScannersData() {
        viewModelScope.launch {
            var type = ""
            var isAvailable = false
            var id = ""
            val scannersList = arrayListOf<Equipment.ProductScanner>()
            document.get().addOnSuccessListener { document ->
                val scanners = document.get(SCANNERS) as List<Map<*,*>>
                scanners.forEach { scanner ->
                    type = scanner[TYPE] as String
                    id = scanner[ID] as String
                    isAvailable = scanner[IS_AVAIABLE] as Boolean
                    if (isAvailable) scannersList.add(Equipment.ProductScanner(type, id, isAvailable))
                }
                _equipmentLiveData.postValue(scannersList)
            }
        }
    }

    fun setSelectedEquipmentId(equipmentId: String) {
        viewModelScope.launch {
            selectedEquipmentId.value = equipmentId
        }
    }

    fun updateUserEquipmentData(currentDepartmentId: String, dataStore: DataStore<LSUserInfo>, userInfo: LSUserInfo, voiceProfile: VoiceProfile) {
        viewModelScope.launch {
            showProgressBar.value = true
            //TODO: update isAvailable field for equipment list
            updateUserInfo(userInfo, dataStore, voiceProfile)
            showProgressBar.value = false
        }
    }

    private fun updateUserInfo(userInfo: LSUserInfo, dataStore: DataStore<LSUserInfo>, voiceProfile: VoiceProfile) {
        viewModelScope.launch {
            collection.document(Constants.USERS).update(
                userInfo.employeeId,
                mapOf(
                    Constants.DEPARTMENT_ID to userInfo.departmentId,
                    Constants.EQUIPMENT_ID to selectedEquipmentId.value,
                    Constants.NAME to userInfo.name,
                    Constants.EMAIL to userInfo.emailAddress,
                    Constants.PIC_URL to userInfo.profilePicUrl,
                    Constants.USER_ID to userInfo.firebaseId,
                    Constants.VOICE_PROFILE to voiceProfile.voiceProfileMap,
                    Constants.OP_TYPE to userInfo.operatorType
                )
            )
            dataStore.updateData {
                it.copy(
                    name = userInfo.name,
                    employeeId = userInfo.employeeId,
                    firebaseId = userInfo.firebaseId,
                    departmentId = userInfo.departmentId,
                    equipmentId = selectedEquipmentId.value,
                    emailAddress = userInfo.emailAddress,
                    profilePicUrl = userInfo.profilePicUrl,
                    operatorType = userInfo.operatorType
                )
            }
        }
    }
}