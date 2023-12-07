package com.clementcorporation.levosonusii.data.remote

import android.util.Log
import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.domain.repositories.MainRepository
import com.clementcorporation.levosonusii.presentation.main.BUSINESSES_ENDPOINT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "MainRepositoryImpl"
class MainRepositoryImpl: MainRepository {
    override fun getBusinessByAddress(addressFromGeocoder: String): Flow<Response<Business>>
    = flow {
        var business: Business? = null
        FirebaseFirestore.getInstance().collection(BUSINESSES_ENDPOINT).get()
            .addOnSuccessListener { businesses ->
                val businessObjects = businesses.toObjects(Business::class.java)
                business = businessObjects.find { it.address == addressFromGeocoder }
                Log.d(TAG, "Business retrieved: ${business?.name}")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to retrieve Business: \n${it.message}")
            }
        if (business != null) {
            emit(Response.Success(data = business))
        }
        else emit(Response.Error("Failed to retrieve company details"))
    }
}