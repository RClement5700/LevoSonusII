package com.clementcorporation.levosonusii.screens.departments

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.*
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class DepartmentsViewModel: ViewModel() {
    private val collection = FirebaseFirestore.getInstance().collection("HannafordFoods")
    private val document = collection.document("departments")
    private val _departmentsLiveData = MutableLiveData<List<Department>>()
    val departmentsLiveData: LiveData<List<Department>> get() = _departmentsLiveData
    val showProgressBar = mutableStateOf(true)
    private val selectedDepartmentId = mutableStateOf("")

    init {
        fetchDepartmentsData()
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
                            "title" -> title = details.value as String
                            "forkliftCount" -> forkliftCount = details.value as String
                            "orderPickerCount" -> orderPickerCount = details.value as String
                            "remainingOrders" -> remainingOrders = details.value as String
                            "icon" -> iconUrl = details.value as String
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

    fun setSelectedDepartment(departmentId: String, userInfo: LSUserInfo, dataStore: DataStore<LSUserInfo>) {
        viewModelScope.launch {
            selectedDepartmentId.value = departmentId
            dataStore.updateData {
                it.copy(
                    name = userInfo.name,
                    employeeId = userInfo.employeeId,
                    firebaseId = userInfo.firebaseId,
                    departmentId = selectedDepartmentId.value,
                    equipmentId = userInfo.equipmentId,
                    emailAddress = userInfo.emailAddress,
                    profilePicUrl = userInfo.profilePicUrl,
                )
            }
        }
    }

    private fun subtractOrderPickerFromDepartment(currentDepartmentId: String) {
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
                            "title" -> currentTitle = details.value as String
                            "forkliftCount" -> currentForkliftCount = details.value as String
                            "orderPickerCount" -> currentOrderPickerCount = details.value as String
                            "remainingOrders" -> currentRemainingOrders = details.value as String
                            "icon" -> currentIconUrl = details.value as String
                        }
                    }
                }
            }
            document.update(
                currentDepartmentId,
                mapOf(
                    "forkliftCount" to currentForkliftCount,
                    "orderPickerCount" to currentOrderPickerCount.toInt().minus(1).toString(),
                    "remainingOrders" to currentRemainingOrders,
                    "title" to currentTitle,
                    "icon" to currentIconUrl
                )
            )
        }
    }

    private fun addOrderPickerToDepartment() {
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
                                "title" -> title = details.value as String
                                "forkliftCount" -> forkliftCount = details.value as String
                                "orderPickerCount" -> orderPickerCount = details.value as String
                                "remainingOrders" -> remainingOrders = details.value as String
                                "icon" -> iconUrl = details.value as String
                            }
                        }
                    }
                }
                document.update(
                    selectedDepartmentId.value,
                    mapOf(
                        "forkliftCount" to forkliftCount,
                        "orderPickerCount" to orderPickerCount.toInt().plus(1).toString(),
                        "remainingOrders" to remainingOrders,
                        "title" to title,
                        "icon" to iconUrl
                    )
                )
            }
        }
    }

    private fun updateUserInfo(userInfo: LSUserInfo, voiceProfile: VoiceProfile) {
        collection.document("users").update(
            userInfo.employeeId,
            mapOf(
                "departmentId" to selectedDepartmentId.value,
                "equipmentId" to userInfo.equipmentId,
                "name" to userInfo.name,
                "emailAddress" to userInfo.emailAddress,
                "profilePicUrl" to userInfo.profilePicUrl,
                "userId" to userInfo.firebaseId,
                "voiceProfile" to voiceProfile.voiceProfileMap
            )
        )
    }

    fun updateUserDepartment(currentDepartmentId: String, userInfo: LSUserInfo, voiceProfile: VoiceProfile) {
        viewModelScope.launch {
            showProgressBar.value = true
            updateUserInfo(userInfo, voiceProfile)
            addOrderPickerToDepartment()
            subtractOrderPickerFromDepartment(currentDepartmentId)
            showProgressBar.value = false
        }
    }
}

class DepartmentsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DepartmentsViewModel() as T
    }
}