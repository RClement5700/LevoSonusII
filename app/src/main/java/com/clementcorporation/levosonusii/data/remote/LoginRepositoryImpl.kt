package com.clementcorporation.levosonusii.data.remote

import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.LoginRepository
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn


private const val TAG = "LoginRepositoryImpl"
class LoginRepositoryImpl: LoginRepository {
    override fun signIn(businessId: String, employeeId: String, password: String): Flow<Response<LSUserInfo>> =
        callbackFlow<Response<LSUserInfo>> {

        awaitClose {
            cancel()
        }
    }.cancellable().flowOn(Dispatchers.IO)
}