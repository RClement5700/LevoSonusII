package com.clementcorporation.levosonusii.data.remote

import android.util.Log
import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.domain.models.LSUserDto
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.toDto
import com.clementcorporation.levosonusii.domain.models.toMap
import com.clementcorporation.levosonusii.domain.repositories.RegisterRepository
import com.clementcorporation.levosonusii.domain.use_cases.SignInUseCase
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
import javax.inject.Inject

private const val TAG = "RegisterRepositoryImpl"
private const val NEW_EMPLOYEE_ID_UPPER_BOUND = 10000
private const val NEW_EMPLOYEE_ID_LOWER_BOUND = 999
class RegisterRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val signInUseCase: SignInUseCase
): RegisterRepository {

    override fun signIn(businessId: String, employeeId: String
    ): Flow<Response<LSUserInfo>> = signInUseCase.invoke(businessId, employeeId)

    override fun register(businessId: String, firstName: String, lastName: String, password: String,
                          email: String): Flow<Response<LSUserInfo>> = callbackFlow {
        send(Response.Loading())
        db.document(businessId).get().addOnSuccessListener { businessSnapshot ->
            val business = businessSnapshot.toObject(Business::class.java)
            db.document(businessId).collection(USERS_ENDPOINT).get().addOnSuccessListener { usersSnapshot ->
                val users = usersSnapshot.toObjects(LSUserDto::class.java)
                val employeeId = "$businessId${(NEW_EMPLOYEE_ID_LOWER_BOUND..NEW_EMPLOYEE_ID_UPPER_BOUND).random()}"
                val isIdInDb = users.find { it.employeeId == employeeId } != null
                if (!isIdInDb) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        email, password).addOnSuccessListener { newUser ->
                        newUser.user?.uid?.let { uid ->
                            business?.let { mBusiness ->
                                val userInfo = LSUserInfo(
                                    name = "$firstName $lastName",
                                    employeeId = employeeId,
                                    firebaseId = uid,
                                    password = password,
                                    emailAddress = email,
                                    organization = mBusiness
                                )
                                db.document(businessId).collection(USERS_ENDPOINT).add(
                                    userInfo.toDto().toMap()
                                ).addOnSuccessListener {
                                    Log.d(TAG, "Successfully Created A New User: ${userInfo.name}")
                                    trySend(Response.Success(data = userInfo))
                                }.addOnFailureListener {
                                    trySend(Response.Error("Failed to add user to database"))
                                    Log.d(TAG, "Failed to add user to database: ${it.message}")
                                }
                            }
                        }
                    }
                } else {
                    register(businessId, firstName, lastName, password, email)
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