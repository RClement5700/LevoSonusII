package com.clementcorporation.levosonusii.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.main.Constants.DEPARTMENT_ID
import com.clementcorporation.levosonusii.main.Constants.EMAIL
import com.clementcorporation.levosonusii.main.Constants.EQUIPMENT_ID
import com.clementcorporation.levosonusii.main.Constants.NAME
import com.clementcorporation.levosonusii.main.Constants.OP_TYPE
import com.clementcorporation.levosonusii.main.Constants.PIC_URL
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
class HomeScreenViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
    ): ViewModel()
{
    private val _operatorTypeLiveData: MutableLiveData<String> = MutableLiveData()
    val operatorTypeLiveData: LiveData<String> get() = _operatorTypeLiveData
    private val collection = FirebaseFirestore.getInstance().collection("HannafordFoods")
    val expandMenu = mutableStateOf(false)
    val showProgressBar = mutableStateOf(false)

    fun getVoiceProfile() = voiceProfileDataStore
    fun getUserInfo() = sessionDataStore
    fun signOut() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            sessionDataStore.updateData {
                LSUserInfo()
            }
            voiceProfileDataStore.updateData {
                VoiceProfile()
            }
            showProgressBar.value = true
            expandMenu.value = false
        }
    }

    fun updateOperatorType(dataStore: DataStore<LSUserInfo>, userInfo: LSUserInfo, voiceProfile: VoiceProfile, operatorType: String) {
        viewModelScope.launch {
            updateUserInfo(userInfo, dataStore, operatorType)
            with(collection) {
                document(USERS).update(
                    userInfo.employeeId,
                    mapOf(
                        DEPARTMENT_ID to userInfo.departmentId,
                        EQUIPMENT_ID to userInfo.equipmentId,
                        NAME to userInfo.name,
                        EMAIL to userInfo.emailAddress,
                        PIC_URL to userInfo.profilePicUrl,
                        USER_ID to userInfo.firebaseId,
                        VOICE_PROFILE to voiceProfile.voiceProfileMap,
                        OP_TYPE to operatorType
                    )
                )
            }
        }
    }

    fun retrieveOperatorType(userInfo: LSUserInfo, dataStore: DataStore<LSUserInfo>) {
        viewModelScope.launch {
            var operatorType = ""
            with(collection) {
                document(USERS).get().addOnSuccessListener { task ->
                    task.data?.forEach {
                        if (it.key == userInfo.employeeId) {
                            val userDetails = it.value as Map<*, *>
                            userDetails.forEach { detail ->
                                when(detail.key) {
                                    OP_TYPE -> operatorType = detail.value as String
                                }
                            }
                        }
                    }
                }
            }
            if (operatorType.isNotEmpty()) {
                updateUserInfo(userInfo, dataStore, operatorType)
                _operatorTypeLiveData.postValue(operatorType)
            }
        }
    }

    private fun updateUserInfo(userInfo: LSUserInfo, dataStore: DataStore<LSUserInfo>, operatorType: String) {
        viewModelScope.launch {
            dataStore.updateData {
                it.copy(
                    name = userInfo.name,
                    employeeId = userInfo.employeeId,
                    firebaseId = userInfo.firebaseId,
                    departmentId = userInfo.departmentId,
                    equipmentId = userInfo.equipmentId,
                    emailAddress = userInfo.emailAddress,
                    profilePicUrl = userInfo.profilePicUrl,
                    operatorType = operatorType
                )
            }
        }
    }
}