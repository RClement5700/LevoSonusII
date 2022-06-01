package com.clementcorporation.levosonusii.screens.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
    ): ViewModel()
{
    private val auth: FirebaseAuth = Firebase.auth

    fun signInWithEmailAndPassword(context: Context, userId: String, password: String, home: () -> Unit = {}) {
        var name: String? = ""
        var email: String? = ""
        var profilePicUrl: String? = ""
        var voiceProfile: Map<*,*>? = hashMapOf<String, ArrayList<String>>()
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance().collection("users")
                    .document(userId).get().addOnCompleteListener { document ->
                        if (document.result.exists()) {
                            name = document.result?.getString("name")
                            email = document.result?.getString("emailAddress")
                            profilePicUrl = document.result?.getString("profilePicUrl")
                            voiceProfile = document.result.data?.get("voiceProfile") as Map<*,*>
                            email?.let { email ->
                                auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                    .addOnCompleteListener { task ->
                                        Log.d("Sign In: ", "SUCCESS")
                                        name?.let { name ->
                                            profilePicUrl?.let { url ->
                                                voiceProfile?.let { voiceProfile ->
                                                    viewModelScope.launch {
                                                        sessionDataStore.updateData { userInfo ->
                                                            userInfo.copy(
                                                                employeeId = userId,
                                                                emailAddress = email,
                                                                name = name,
                                                                profilePicUrl = url
                                                            )
                                                        }
                                                        voiceProfileDataStore.updateData { vp ->
                                                            vp.copy(voiceProfileMap = voiceProfile as HashMap<String, ArrayList<String>>)
                                                        }
                                                        home()
                                                    }
                                                }
                                            }
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(context, "Invalid Employee ID or Password", Toast.LENGTH_LONG).show()
                        }
                    }
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    Log.d("Sign In: ", it)
                }
            }
        }
    }
}