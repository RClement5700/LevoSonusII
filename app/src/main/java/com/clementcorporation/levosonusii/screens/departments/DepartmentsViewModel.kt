package com.clementcorporation.levosonusii.screens.departments

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class DepartmentsViewModel: ViewModel() {
    private val document = FirebaseFirestore.getInstance()
        .collection("HannafordFoods")
        .document("departments")
    private val _departmentsLiveData = MutableLiveData<List<Department>>()
    val departmentsLiveData: LiveData<List<Department>> get() = _departmentsLiveData
    val showProgressBar = mutableStateOf(false)

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
            }
        }
    }
}

class DepartmentsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DepartmentsViewModel() as T
    }
}