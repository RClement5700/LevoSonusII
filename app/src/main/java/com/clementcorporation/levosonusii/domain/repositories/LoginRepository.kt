package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun signIn(businessId: String, employeeId: String, password: String): Flow<Response<LSUserInfo>>
}