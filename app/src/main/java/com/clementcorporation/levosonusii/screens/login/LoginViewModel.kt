package com.clementcorporation.levosonusii.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(userId: String, password: String, home: () -> Unit = {}) {
        var email: String? = ""
        viewModelScope.launch {
            _loading.value = true
            try {
                /*
                    TODO: -get email from Firebase based on corresponding employee Id
                          -update navController once sign in is completed
                 */
                email = FirebaseFirestore.getInstance().collection("users")
                    .document(userId).get().result?.getString("emailAddress")
                println("Email: $email")
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    Log.d("Sign In: ", it)
                }
            }
        }.invokeOnCompletion { throwable ->
            try {
                email?.let {
                    auth.signInWithEmailAndPassword(it.trim(), password.trim()).addOnCompleteListener{
                            task -> Log.d("Sign In: ", "SUCCESS")
                        home()
                    }
                }
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    Log.d("Sign In: ", it)
                }
            }
        }
        _loading.value = false
    }
}