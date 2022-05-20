package com.clementcorporation.levosonusii.screens.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.model.LevoSonusUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun createUserWithEmailAndPassword(context: Context, email: String, password: String, home: () -> Unit = {}) {
        if (_loading.value == false) {
            _loading.value = true
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful) {
                            try {
                                task.result?.user?.email?.let {
                                    createUser(emailAddress = it)
                                }
                                home()
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

    fun signInWithEmailAndPassword(userId: String, password: String, home: () -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            try {
                auth.signInWithEmailAndPassword(userId.trim(), password.trim()).addOnCompleteListener{
                        task -> Log.d("Sign In: ", "SUCCESS")
                    home()
                }
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    Log.d("Sign In: ", it)
                }
            }
            _loading.value = false
        }
    }

    private fun createUser(emailAddress: String) {
        val userId = auth.currentUser?.uid
        val lsUser = LevoSonusUser(
            userId = userId.toString(),
            emailAddress = emailAddress
        ).toMap()
        FirebaseFirestore.getInstance().collection("users").document(
            (0..10000).random().toString()
        ).get().isSuccessful





//            .add(lsUser)
    }
}