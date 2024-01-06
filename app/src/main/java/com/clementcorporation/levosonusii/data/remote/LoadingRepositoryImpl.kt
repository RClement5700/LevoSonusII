package com.clementcorporation.levosonusii.data.remote

import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.domain.repositories.LoadingRepository
import com.clementcorporation.levosonusii.domain.use_cases.GetCompanyAddressUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadingRepositoryImpl @Inject constructor(private val getCompanyAddressUseCase: GetCompanyAddressUseCase
): LoadingRepository {
    override fun getBusinessByAddress(addressFromGeocoder: String): Flow<Business?>
        = getCompanyAddressUseCase.invoke(addressFromGeocoder)
}