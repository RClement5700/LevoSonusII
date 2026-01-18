package com.clementcorporation.levosonusii.domain.use_cases

import android.util.Log
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn

private const val TAG = "AuthenticateUseCase"
class AuthenticateUseCase {

    operator fun invoke(email: String, password: String) = callbackFlow {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { response ->
                val user = response.user
                Log.d(TAG, "Sign In Success")
                trySend(Response.Success(data = user))
            }.addOnFailureListener {
                trySend(Response.Error("Employee ID or password are incorrect"))
                Log.d(TAG, "Employee ID or password are incorrect:\n${it.message}")
            }
        awaitClose {
            cancel()
        }
    }.cancellable().flowOn(Dispatchers.IO)
}