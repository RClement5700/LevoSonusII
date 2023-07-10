package com.clementcorporation.levosonusii.screens.departments

import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.*
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.DEPARTMENTS
import com.clementcorporation.levosonusii.main.Constants.DEPARTMENT_ID
import com.clementcorporation.levosonusii.main.Constants.EMAIL
import com.clementcorporation.levosonusii.main.Constants.FORKLIFT_COUNT
import com.clementcorporation.levosonusii.main.Constants.HEADSET_ID
import com.clementcorporation.levosonusii.main.Constants.ICON_URL
import com.clementcorporation.levosonusii.main.Constants.MACHINE_ID
import com.clementcorporation.levosonusii.main.Constants.NAME
import com.clementcorporation.levosonusii.main.Constants.OP_COUNT
import com.clementcorporation.levosonusii.main.Constants.OP_TYPE
import com.clementcorporation.levosonusii.main.Constants.PIC_URL
import com.clementcorporation.levosonusii.main.Constants.REMAINING_ORDERS
import com.clementcorporation.levosonusii.main.Constants.SCANNER_ID
import com.clementcorporation.levosonusii.main.Constants.TITLE
import com.clementcorporation.levosonusii.main.Constants.USERS
import com.clementcorporation.levosonusii.main.Constants.USER_ID
import com.clementcorporation.levosonusii.main.Constants.VOICE_PROFILE
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class DepartmentsViewModel(private val resources: Resources): ViewModel() {
    private val collection = FirebaseFirestore.getInstance().collection("HannafordFoods")
    private val document = collection.document(DEPARTMENTS)
    private val _departmentsLiveData = MutableLiveData<List<Department>>()
    val departmentsLiveData: LiveData<List<Department>> get() = _departmentsLiveData
    private val _currentDepartmentIdLiveData = MutableLiveData<String>()
    val currentDepartmentIdLiveData: LiveData<String> get() = _currentDepartmentIdLiveData
    val showProgressBar = mutableStateOf(true)
    private val selectedDepartmentId = mutableStateOf("")

    init {
        fetchDepartmentsData()
    }

    fun fetchCurrentDepartmentId(userInfo: LSUserInfo) {
        viewModelScope.launch {
            collection.document(USERS).get().addOnSuccessListener { task ->
                task.data?.forEach {
                    if (it.key == userInfo.employeeId) {
                        val userDetails = it.value as Map<*, *>
                        userDetails.forEach { details ->
                            when (details.key) {
                                DEPARTMENT_ID -> _currentDepartmentIdLiveData.postValue(details.value as String)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun fetchDepartmentsData() {
        viewModelScope.launch {
            showProgressBar.value = true
            document.get().addOnSuccessListener { task ->
                showProgressBar.value = false
                val departments = arrayListOf<Department>()
                task.data?.forEach {
                    val id = it.key
                    var title = ""
                    var forkliftCount = ""
                    var orderPickerCount = ""
                    var remainingOrders = ""
                    var iconUrl = ""
                    val departmentDetails = (it.value as Map<*, *>)
                    departmentDetails.forEach { details ->
                        when (details.key) {
                            TITLE -> title = details.value as String
                            FORKLIFT_COUNT -> forkliftCount = details.value as String
                            OP_COUNT -> orderPickerCount = details.value as String
                            REMAINING_ORDERS -> remainingOrders = details.value as String
                            ICON_URL -> iconUrl = details.value as String
                        }
                    }
                    departments.add(Department(
                        id = id,
                        title = title,
                        forkliftCount = forkliftCount,
                        orderPickersCount = orderPickerCount,
                        remainingOrders = remainingOrders,
                        iconUrl = iconUrl
                    ))
                }
                _departmentsLiveData.postValue(departments.toList())
                showProgressBar.value = false
            }
        }
    }

    fun setSelectedDepartment(departmentId: String) {
        viewModelScope.launch {
            selectedDepartmentId.value = departmentId
        }
    }

    private fun subtractOrderPickerFromDepartment(currentDepartmentId: String, userInfo: LSUserInfo) {
        document.get().addOnSuccessListener { task ->
            var currentTitle = ""
            var currentForkliftCount = ""
            var currentOrderPickerCount = ""
            var currentRemainingOrders = ""
            var currentIconUrl = ""
            task.data?.forEach {
                val id = it.key
                val departmentDetails = (it.value as Map<*, *>)
                if (currentDepartmentId == id) {
                    departmentDetails.forEach { details ->
                        when (details.key) {
                            TITLE -> currentTitle = details.value as String
                            FORKLIFT_COUNT -> currentForkliftCount = details.value as String
                            OP_COUNT -> currentOrderPickerCount = details.value as String
                            REMAINING_ORDERS -> currentRemainingOrders = details.value as String
                            ICON_URL -> currentIconUrl = details.value as String
                        }
                    }
                }
            }
            document.update(
                currentDepartmentId,
                mapOf(
                    FORKLIFT_COUNT to if (userInfo.operatorType == resources.getString(R.string.operator_type_forklift_tile_text))
                        currentForkliftCount.toInt().minus(1).toString()
                    else currentForkliftCount,
                    OP_COUNT to if (userInfo.operatorType == resources.getString(R.string.operator_type_order_picker_tile_text))
                        currentOrderPickerCount.toInt().minus(1).toString()
                    else currentOrderPickerCount,
                    REMAINING_ORDERS to currentRemainingOrders,
                    TITLE to currentTitle,
                    ICON_URL to currentIconUrl
                )
            )
        }
    }

    private fun addOrderPickerToDepartment(userInfo: LSUserInfo) {
        if (selectedDepartmentId.value.isNotEmpty()) {
            document.get().addOnSuccessListener { task ->
                var title = ""
                var forkliftCount = ""
                var orderPickerCount = ""
                var remainingOrders = ""
                var iconUrl = ""
                task.data?.forEach {
                    val id = it.key
                    val departmentDetails = (it.value as Map<*, *>)
                    if (selectedDepartmentId.value == id) {
                        departmentDetails.forEach { details ->
                            when (details.key) {
                                TITLE -> title = details.value as String
                                FORKLIFT_COUNT -> forkliftCount = details.value as String
                                OP_COUNT -> orderPickerCount = details.value as String
                                REMAINING_ORDERS -> remainingOrders = details.value as String
                                ICON_URL -> iconUrl = details.value as String
                            }
                        }
                    }
                }
                document.update(
                    selectedDepartmentId.value,
                    mapOf(
                        FORKLIFT_COUNT to if (userInfo.operatorType == resources.getString(R.string.operator_type_forklift_tile_text))
                            forkliftCount.toInt().plus(1).toString()
                        else forkliftCount,
                        OP_COUNT to if (userInfo.operatorType == resources.getString(R.string.operator_type_order_picker_tile_text))
                            orderPickerCount.toInt().plus(1).toString()
                        else orderPickerCount,
                        REMAINING_ORDERS to remainingOrders,
                        TITLE to title,
                        ICON_URL to iconUrl
                    )
                )
            }
        }
    }

    private fun updateUserInfo(userInfo: LSUserInfo, dataStore: DataStore<LSUserInfo>, voiceProfile: VoiceProfile) {
        viewModelScope.launch {
            collection.document(USERS).update(
                userInfo.employeeId,
                mapOf(
                    DEPARTMENT_ID to selectedDepartmentId.value,
                    MACHINE_ID to userInfo.machineId,
                    HEADSET_ID to userInfo.headsetId,
                    SCANNER_ID to userInfo.scannerId,
                    NAME to userInfo.name,
                    EMAIL to userInfo.emailAddress,
                    PIC_URL to userInfo.profilePicUrl,
                    USER_ID to userInfo.firebaseId,
                    VOICE_PROFILE to voiceProfile.voiceProfileMap,
                    OP_TYPE to userInfo.operatorType
                )
            )
            dataStore.updateData {
                it.copy(
                    name = userInfo.name,
                    employeeId = userInfo.employeeId,
                    firebaseId = userInfo.firebaseId,
                    departmentId = selectedDepartmentId.value,
                    machineId = userInfo.machineId,
                    headsetId = userInfo.headsetId,
                    scannerId = userInfo.scannerId,
                    emailAddress = userInfo.emailAddress,
                    profilePicUrl = userInfo.profilePicUrl,
                    operatorType = userInfo.operatorType
                )
            }
        }
    }

    fun updateUserDepartment(currentDepartmentId: String, dataStore: DataStore<LSUserInfo>, userInfo: LSUserInfo, voiceProfile: VoiceProfile) {
        viewModelScope.launch {
            showProgressBar.value = true
            updateUserInfo(userInfo, dataStore, voiceProfile)
            addOrderPickerToDepartment(userInfo)
            subtractOrderPickerFromDepartment(currentDepartmentId, userInfo)
            showProgressBar.value = false
        }
    }
}

class DepartmentsViewModelFactory(private val resources: Resources): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DepartmentsViewModel(resources) as T
    }
}