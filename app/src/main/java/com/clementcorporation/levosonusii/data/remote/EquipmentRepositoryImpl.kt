package com.clementcorporation.levosonusii.data.remote

import com.clementcorporation.levosonusii.domain.models.EquipmentDto
import com.clementcorporation.levosonusii.domain.models.EquipmentUiModel
import com.clementcorporation.levosonusii.domain.models.toEquipmentUiModel
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.util.Constants.BUSINESSES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.HEADSETS_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.MACHINES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.SCANNERS_ENDPOINT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class EquipmentRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
): EquipmentRepository {
    val businessesRef = db.collection(BUSINESSES_ENDPOINT)

    private fun getEquipment(
        businessId: String,
        equipmentEndpoint: String,
        errorMessage: String
    ): Flow<Response<List<EquipmentUiModel>>> = callbackFlow {
            if (businessId.isBlank()) {
                trySend(Response.Error("Invalid business ID"))
                close()
                return@callbackFlow
            }
            val businessDocRef = businessesRef.document(businessId)
            businessDocRef.collection(equipmentEndpoint).get()
                .addOnSuccessListener { result ->
                    val equipmentDtos = result.toObjects(EquipmentDto::class.java)
                    val equipment = equipmentDtos
                        .filter { it.isAvailable }
                        .sortedBy { it.serialNumber }
                        .map { it.toEquipmentUiModel() }
                    if (equipment.isNotEmpty()) {
                        trySend(Response.Success(equipment))
                    } else {
                        trySend(Response.Error(errorMessage))
                    }
                }.addOnFailureListener { failure ->
                    trySend(Response.Error(failure.message.orEmpty()))
                }.addOnCompleteListener {
                    close()
                }
            awaitClose {
                cancel()
            }
        }
    override fun getHeadsets(businessId: String) =
        getEquipment(
            businessId = businessId,
            equipmentEndpoint = HEADSETS_ENDPOINT,
            errorMessage = "Failed to retrieve data for headsets"
        )

    override fun getScanners(businessId: String) =
        getEquipment(
            businessId = businessId,
            equipmentEndpoint = SCANNERS_ENDPOINT,
            errorMessage = "Failed to retrieve data for scanners"
        )

    override fun getMachines(businessId: String) =
        getEquipment(
            businessId = businessId,
            equipmentEndpoint = MACHINES_ENDPOINT,
            errorMessage = "Failed to retrieve data for machines"
        )
}