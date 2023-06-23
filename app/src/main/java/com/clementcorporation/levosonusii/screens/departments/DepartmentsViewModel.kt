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

    fun updateUserDepartment(userInfo: LSUserInfo, voiceProfile: VoiceProfile) {
        viewModelScope.launch {
            if (selectedDepartmentId.value.isNotEmpty()) {
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
                showProgressBar.value = false
            }
        }
    }
}

class DepartmentsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DepartmentsViewModel() as T
    }
}