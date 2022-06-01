package com.clementcorporation.levosonusii.screens.register

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.LevoSonusUser
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.screens.voiceprofile.VoiceProfileCommands
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
    ): ViewModel()
{
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    val employeeId = mutableStateOf("")

    fun createUserWithEmailAndPassword(context: Context, email: String, password: String, firstName: String,
                                       lastName: String, goToNextScreen: () -> Unit = {}) {
        if (_loading.value == false) {
            _loading.value = true
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful) {
                            try {
                                task.result?.user?.email?.let {
                                    createUser(emailAddress = it, firstName = firstName.trim(), lastName = lastName.trim())
                                }
                                goToNextScreen()
                            } catch(e: Exception) {
                                Toast.makeText(context, "Cannot Create User", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Log.d("Create User: ", task.result.toString())
                        }
                        _loading.value = false
                    }
            } catch (e: Exception) {
                Toast.makeText(context, "Cannot Create User", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUser(emailAddress: String, firstName: String, lastName: String) {
        val userId = auth.currentUser?.uid
        val voiceProfile: HashMap<String, ArrayList<String>> = hashMapOf()
        VoiceProfileCommands.values().forEach {
            voiceProfile[it.name] = arrayListOf()
        }
        val lsUser = LevoSonusUser(
            userId = userId.toString(),
            emailAddress = emailAddress,
            name = "$firstName $lastName",
            voiceProfile = voiceProfile
        //WHY ISN'T THE PROFILEPICURL BEING ADDED TO THE MAP UPON CREATION OF NEW USER?
        ).toMap()
        employeeId.value = (999..10000).random().toString()
        viewModelScope.launch {
            sessionDataStore.updateData { userInfo ->
                userInfo.copy(
                    employeeId = employeeId.value,
                    name = "$firstName $lastName",
                    emailAddress = emailAddress
                )
            }
            voiceProfileDataStore.updateData { vp ->
                vp.copy(voiceProfileMap = voiceProfile)
            }
        }
        FirebaseFirestore.getInstance().collection("users").document(employeeId.value).set(lsUser)
    }
}