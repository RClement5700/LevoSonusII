package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.Business
import kotlinx.coroutines.flow.Flow

interface LoadingRepository {
    fun getBusinessByAddress(addressFromGeocoder: String): Flow<Business?>
}