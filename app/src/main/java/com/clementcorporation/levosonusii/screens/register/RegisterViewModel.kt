package com.clementcorporation.levosonusii.screens.register

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clementcorporation.levosonusii.model.LevoSonusUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterViewModel: ViewModel() {
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