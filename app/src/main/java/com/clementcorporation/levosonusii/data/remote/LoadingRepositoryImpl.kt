package com.clementcorporation.levosonusii.data.remote

import android.util.Log
import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.domain.repositories.LoadingRepository
import com.clementcorporation.levosonusii.util.Constants.BUSINESSES_ENDPOINT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn

private const val TAG = "LoadingRepositoryImpl"
class LoadingRepositoryImpl: LoadingRepository {
    override fun getBusinessByAddress(addressFromGeocoder: String): Flow<Response<Business>>
    = callbackFlow {
        FirebaseFirestore.getInstance().collection(BUSINESSES_ENDPOINT).get()
            .addOnSuccessListener { businesses ->
                val businessObjects = businesses.toObjects(Business::class.java)
                val business = businessObjects.find { it.address == addressFromGeocoder }
                Log.d(TAG, "Business retrieved: ${business?.name}")
                trySend(Response.Success(data = business))
            }
            .addOnFailureListener {
                trySend(Response.Error("Failed to retrieve company details"))
                Log.d(TAG, "Failed to retrieve Business: \n${it.message}")
            }
        awaitClose {
            cancel()
        }
    }.cancellable().flowOn(Dispatchers.IO)
}