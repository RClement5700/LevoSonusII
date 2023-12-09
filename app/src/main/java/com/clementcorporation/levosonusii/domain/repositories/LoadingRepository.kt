package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface LoadingRepository {
    fun getBusinessByAddress(addressFromGeocoder: String): Flow<Response<Business>>
}