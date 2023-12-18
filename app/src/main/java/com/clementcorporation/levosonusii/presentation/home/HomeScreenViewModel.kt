package com.clementcorporation.levosonusii.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.VoiceProfile
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.Constants.DEPARTMENT_ID
import com.clementcorporation.levosonusii.util.Constants.EMAIL
import com.clementcorporation.levosonusii.util.Constants.NAME
import com.clementcorporation.levosonusii.util.Constants.OP_TYPE
import com.clementcorporation.levosonusii.util.Constants.PIC_URL
import com.clementcorporation.levosonusii.util.Constants.USERS
import com.clementcorporation.levosonusii.util.Constants.USER_ID
import com.clementcorporation.levosonusii.util.Constants.VOICE_PROFILE
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>,
    private val signOutUseCase: SignOutUseCase
): ViewModel() {
    private lateinit var collection: CollectionReference
    val expandMenu = mutableStateOf(false)
    val showProgressBar = mutableStateOf(false)
    val showOperatorTypeWindow = mutableStateOf(false)
    val inflateProfilePic = mutableStateOf(false)
    val operatorType = mutableStateOf("")

    fun getUserInfo() = sessionDataStore

    fun signOut(navigate: () -> Unit) {
        viewModelScope.launch {
            showProgressBar.value = true
            expandMenu.value = false
            signOutUseCase.invoke(navigate)
        }
    }

    fun updateOperatorType() {
        viewModelScope.launch {
            sessionDataStore.data.collect { info ->
                voiceProfileDataStore.data.collect { vpDataStore ->
                    updateUserInfo(operatorType.value)
                    with(collection) {
                        document(USERS).update(
                            info.employeeId,
                            mapOf(
                                DEPARTMENT_ID to info.departmentId,
                                Constants.MACHINE_ID to info.machineId,
                                Constants.HEADSET_ID to info.headsetId,
                                Constants.SCANNER_ID to info.scannerId,
                                NAME to info.name,
                                EMAIL to info.emailAddress,
                                PIC_URL to info.profilePicUrl,
                                USER_ID to info.firebaseId,
                                VOICE_PROFILE to vpDataStore.voiceProfileMap,
                                OP_TYPE to operatorType
                            )
                        )
                    }
                }
            }
        }
    }

    private fun retrieveOperatorType() {
        viewModelScope.launch {
            sessionDataStore.data.collect { info ->
                var operatorType = ""
                with(collection) {
                    document(USERS).get().addOnSuccessListener { task ->
                        task.data?.forEach {
                            if (it.key == info.employeeId) {
                                val userDetails = it.value as Map<*, *>
                                userDetails.forEach { detail ->
                                    when (detail.key) {
                                        OP_TYPE -> operatorType = detail.value as String
                                    }
                                }
                            }
                        }
                    }
                }
                if (operatorType.isNotEmpty()) {
                    updateUserInfo(operatorType)
                }
            }
        }
    }

    private fun updateUserInfo(operatorType: String) {
        viewModelScope.launch {
            sessionDataStore.updateData { info ->
                info.copy(
                    name = info.name,
                    employeeId = info.employeeId,
                    firebaseId = info.firebaseId,
                    departmentId = info.departmentId,
                    machineId = info.machineId,
                    headsetId = info.headsetId,
                    scannerId = info.scannerId,
                    emailAddress = info.emailAddress,
                    profilePicUrl = info.profilePicUrl,
                    operatorType = operatorType
                )
            }
        }
    }
}