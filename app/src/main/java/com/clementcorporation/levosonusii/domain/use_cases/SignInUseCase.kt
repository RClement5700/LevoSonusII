package com.clementcorporation.levosonusii.domain.use_cases

import android.util.Log
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Constants.BUSINESSES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.USERS_ENDPOINT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.firestore.FirebaseFirestore
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
    private val db: FirebaseFirestore
){
    operator fun invoke(businessId: String, employeeId: String):
            Flow<Response<LSUserInfo>> = callbackFlow {
        send(Response.Loading())
        db.collection(BUSINESSES_ENDPOINT).document(businessId).collection(USERS_ENDPOINT).get()
            .addOnSuccessListener { users ->
                val userObjects = users.toObjects(LSUserInfo::class.java)
                val user = userObjects.find { it.employeeId == employeeId }
                trySend(Response.Success(data = user))
            }.addOnFailureListener {
                trySend(Response.Error("Failed to retrieve user credentials"))
                Log.d(TAG, "Failed to retrieve user $employeeId\n${it.message}")
            }
        awaitClose {
            cancel()
        }
    }.cancellable().flowOn(Dispatchers.IO)
}