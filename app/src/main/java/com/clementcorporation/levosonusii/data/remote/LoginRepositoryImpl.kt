package com.clementcorporation.levosonusii.data.remote

import android.util.Log
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.LoginRepository
import com.clementcorporation.levosonusii.util.Constants.BUSINESSES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.USERS_ENDPOINT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn

private const val TAG = "LoginRepositoryImpl"
class LoginRepositoryImpl: LoginRepository {
    //TODO: add VoiceProfile to LSUserInfo and a corresponding counterpart in the Firestore database
    override fun signIn(businessId: String, employeeId: String, password: String): Flow<Response<LSUserInfo>> =
        callbackFlow {
            send(Response.Loading())
            FirebaseFirestore.getInstance().collection(BUSINESSES_ENDPOINT)
                .document(businessId).collection(USERS_ENDPOINT).get()
                .addOnSuccessListener { users ->
                    val userObjects = users.toObjects(LSUserInfo::class.java)
                    val user = userObjects.find { it.employeeId == employeeId }
                    user?.emailAddress?.let { email ->
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                Log.d(TAG, "Sign In Success: ${user.name}")
                                trySend(Response.Success(data = user))
                            }.addOnFailureListener {
                                trySend(Response.Error("Email or password are incorrect"))
                                Log.d(TAG, "Email or password are incorrect:\n${it.message}")
                            }
                    }
                }.addOnFailureListener {
                    trySend(Response.Error("Failed to retrieve user credentials"))
                    Log.d(TAG, "Failed to retrieve user $employeeId\n${it.message}")
                }
            awaitClose {
                cancel()
            }
        }.cancellable().flowOn(Dispatchers.IO)
}