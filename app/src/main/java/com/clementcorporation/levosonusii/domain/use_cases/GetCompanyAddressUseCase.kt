package com.clementcorporation.levosonusii.domain.use_cases

import android.util.Log
import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.util.Constants.BUSINESSES_ENDPOINT
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

private const val TAG = "GetCompanyAddressUseCase"
class GetCompanyAddressUseCase {
    operator fun invoke(addressFromGeocoder: String): Flow<Business?>
        = FirebaseFirestore.getInstance().collection(BUSINESSES_ENDPOINT).snapshots()
            .map { snapshot ->
                snapshot.toObjects(Business::class.java).find { business ->
                    business.address == addressFromGeocoder
                }
            }.catch {
                Log.d(TAG, "Failed to retrieve Business: \n${it.message}")
            }.flowOn(Dispatchers.IO).cancellable()
}