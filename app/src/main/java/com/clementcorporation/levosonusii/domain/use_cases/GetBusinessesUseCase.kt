package com.clementcorporation.levosonusii.domain.use_cases

import com.clementcorporation.levosonusii.domain.models.Business
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

open class GetBusinessesUseCase @Inject constructor(
    private val db: CollectionReference
) {
    operator fun invoke(): Flow<Response<List<Business?>>> =
        db.snapshots().map {
            val businesses = it.toObjects(Business::class.java)
            if (businesses.isEmpty()) Response.Error("Failed to retrieve Businesses")
            else Response.Success(businesses)
        }
}