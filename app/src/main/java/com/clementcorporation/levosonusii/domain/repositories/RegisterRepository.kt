package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    fun register(business: Business, firstName: String, lastName: String, password: String, email: String
        ): Flow<Response<LSUserInfo>>
}