package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    fun signIn(businessId: String, employeeId: String): Flow<Response<LSUserInfo>>
    fun register(businessId: String, firstName: String, lastName: String, password: String, email: String
        ): Flow<Response<LSUserInfo>>
}