package com.clementcorporation.levosonusii.screens.register

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.VoiceProfileConstants
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.LevoSonusUser
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.util.AuthenticationUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val EMAIL_VALIDATOR_AT = "@"
private const val EMAIL_VALIDATOR_COM = ".com"
private const val NEW_EMPLOYEE_ID_UPPER_BOUND = 10000
private const val NEW_EMPLOYEE_ID_LOWER_BOUND = 999

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
): ViewModel() {
    val loading = mutableStateOf(false)
    val employeeId = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val isRegisterButtonEnabled = mutableStateOf(false)
    val showNewUserDialog = mutableStateOf(false)
    val showVoiceProfileDialog = mutableStateOf(false)
    
    fun validateInputs(): Boolean {
        return email.value.contains(EMAIL_VALIDATOR_AT) && email.value.contains(EMAIL_VALIDATOR_COM) && email.value.isNotEmpty() && password.value.isNotEmpty()
                && firstName.value.isNotEmpty() && lastName.value.isNotEmpty()
    }

    fun createUser(context: Context) {
        viewModelScope.launch {
            loading.value = true
            val userId = Firebase.auth.currentUser?.uid
            val voiceProfile: HashMap<String, ArrayList<String>> = hashMapOf()
            VoiceProfileConstants.values().forEach {
                voiceProfile[it.name] = arrayListOf()
            }
            val lsUser = LevoSonusUser(
                userId = userId.toString(),
                emailAddress = email.value,
                name = "${firstName.value} ${lastName.value}",
                voiceProfile = voiceProfile,
            ).toMap()
            sessionDataStore.data.collect{
                val doc = FirebaseFirestore.getInstance().collection(it.organization.name).document("users")
                employeeId.value = (NEW_EMPLOYEE_ID_LOWER_BOUND..NEW_EMPLOYEE_ID_UPPER_BOUND).random().toString()
                doc.get().addOnSuccessListener { users ->
                    val emailAddresses = arrayListOf<String>()
                    val employeeIds = arrayListOf<String>()
                    users.data?.forEach { user ->
                        val employeeIdFromFirebase = user.key
                        employeeIds.add(employeeIdFromFirebase)
                        var emailAddressFromFirebase = ""
                        val userDetails = user.value as Map<*,*>
                        userDetails.forEach { detail ->
                            when(detail.key) {
                                Constants.EMAIL -> emailAddressFromFirebase = detail.value as String
                            }
                        }
                        emailAddresses.add(emailAddressFromFirebase)
                    }
                    if (emailAddresses.contains(email.value) ||
                        employeeIds.contains(employeeId.value)
                    ) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.register_screen_user_already_exists_toast_message),
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Firebase.auth.createUserWithEmailAndPassword(email.value.trim(), password.value.trim())
                            .addOnCompleteListener {
                                viewModelScope.launch {
                                    AuthenticationUtil.signOut(sessionDataStore, voiceProfileDataStore)
                                    sessionDataStore.updateData { userInfo ->
                                        userInfo.copy(
                                            employeeId = employeeId.value,
                                            name = "${firstName.value} ${lastName.value}",
                                            emailAddress = email.value
                                        )
                                    }
                                    voiceProfileDataStore.updateData { vp ->
                                        vp.copy(voiceProfileMap = voiceProfile)
                                    }
                                    doc.update(employeeId.value, lsUser)
                                    showNewUserDialog.value = true
                                }
                            }
                    }
                }
                loading.value = false
            }
        }
    }
}