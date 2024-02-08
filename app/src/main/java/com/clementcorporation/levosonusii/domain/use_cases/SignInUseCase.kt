package com.clementcorporation.levosonusii.domain.use_cases

import android.util.Log
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Constants.USERS_ENDPOINT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

private const val TAG = "SignInUseCase"
class SignInUseCase @Inject constructor(
    private val db: CollectionReference
){
    operator fun invoke(businessId: String, employeeId: String, password: String):
            Flow<Response<LSUserInfo>> = callbackFlow {
        send(Response.Loading())
        db.document(businessId).collection(USERS_ENDPOINT).get()
            .addOnSuccessListener { users ->
                val userObjects = users.toObjects(LSUserInfo::class.java)
                val user = userObjects.find { it.employeeId == employeeId }
                if (user?.emailAddress?.isNotEmpty() == true) {
                    val email = user.emailAddress
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Log.d(TAG, "Sign In Success: ${user.name}")
                            trySend(Response.Success(data = user))
                        }.addOnFailureListener {
                            trySend(Response.Error("Employee ID or password are incorrect"))
                            Log.d(TAG, "Employee ID or password are incorrect:\n${it.message}")
                        }
                } else {
                    trySend(Response.Error("Failed to retrieve user credentials"))
                    Log.d(TAG, "Failed to retrieve user $employeeId")
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