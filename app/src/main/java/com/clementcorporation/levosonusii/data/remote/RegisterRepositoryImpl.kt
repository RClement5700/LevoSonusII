package com.clementcorporation.levosonusii.data.remote

import android.util.Log
import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.toMap
import com.clementcorporation.levosonusii.domain.repositories.RegisterRepository
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

private const val TAG = "RegisterRepositoryImpl"
private const val NEW_EMPLOYEE_ID_UPPER_BOUND = 10000
private const val NEW_EMPLOYEE_ID_LOWER_BOUND = 999
class RegisterRepositoryImpl: RegisterRepository {
    override fun register(business: Business, firstName: String, lastName: String, password: String,
                          email: String): Flow<Response<LSUserInfo>> = callbackFlow {
        send(Response.Loading())
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            email, password).addOnSuccessListener { newUser ->
            val employeeId = (NEW_EMPLOYEE_ID_LOWER_BOUND..NEW_EMPLOYEE_ID_UPPER_BOUND).random().toString()
            newUser.user?.uid?.let { uid ->
                val userInfo = LSUserInfo(
                        name = "$firstName $lastName",
                        employeeId = employeeId,
                        firebaseId = uid,
                        password = password,
                        emailAddress = email
                    )
                FirebaseFirestore.getInstance().collection(BUSINESSES_ENDPOINT)
                    .document(business.id).collection(USERS_ENDPOINT).add(
                        userInfo.toMap()
                    ).addOnSuccessListener {
                        Log.d(TAG, "Successfully Created A New User: ${userInfo.name}")
                        trySend(Response.Success(data = userInfo))
                    }.addOnFailureListener {
                        trySend(Response.Error("Failed to add user to database"))
                        Log.d(TAG, "Failed to add user to database: ${it.message}")
                    }
            }
        }.addOnFailureListener {
            trySend(Response.Error("Failed to create new user"))
            Log.d(TAG, "Failed to create new user: ${it.message}")
        }
        awaitClose {
            cancel()
        }
    }.cancellable().flowOn(Dispatchers.IO)
}