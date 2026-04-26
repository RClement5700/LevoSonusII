package com.clementcorporation.levosonusii.data.remote

import android.util.Log
import com.clementcorporation.levosonusii.domain.models.EquipmentDto
import com.clementcorporation.levosonusii.domain.models.EquipmentUiModel
import com.clementcorporation.levosonusii.domain.models.toEquipmentUiModel
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.util.Constants.BUSINESSES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.IS_AVAILABLE_KEY
import com.clementcorporation.levosonusii.util.Constants.MACHINES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.USERS_ENDPOINT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

private const val TAG = "EquipmentRepositoryImpl"
class EquipmentRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
): EquipmentRepository {
    val businessesRef = db.collection(BUSINESSES_ENDPOINT)

    override fun getEquipment(
        businessId: String,
        equipmentId: String,
        equipmentEndpoint: String
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
                val myEquipment = equipmentDtos.find {
                    dto -> dto.serialNumber == equipmentId
                }?.toEquipmentUiModel()
                var equipment = equipmentDtos
                    .map { uiModel -> uiModel.toEquipmentUiModel() }
                    .filter { uiModel -> uiModel.isAvailable }
                    .sortedBy { uiModel -> uiModel.serialNumber }
                myEquipment?.let { equipment = equipment.toMutableList().apply {
                    add(0, it)
                }.toList().distinctBy { listItem ->
                    if (equipmentEndpoint == MACHINES_ENDPOINT) listItem.id else listItem.serialNumber
                }}
                if (equipment.isNotEmpty()) {
                    trySend(Response.Success(equipment))
                } else {
                    trySend(Response.Error("$equipmentEndpoint returned empty list"))
                }
            }.addOnFailureListener { failure ->
                trySend(Response.Error("Failed to retrieve data for $equipmentEndpoint: ${failure.message.orEmpty()}"))
            }.addOnCompleteListener {
                close()
            }
        awaitClose {
            cancel()
        }
    }

    override fun setEquipmentId(
        businessId: String,
        firebaseId: String,
        equipmentKey: String,
        newEquipment: EquipmentUiModel,
        currentEquipment: EquipmentUiModel,
        equipmentEndpoint: String
    ): Flow<Response<Boolean>> = callbackFlow {
        if (businessId.isBlank()) {
            trySend(Response.Error("Invalid business ID"))
            close()
            return@callbackFlow
        }
        val currentDocId = currentEquipment.id
        val currentSerialNumber = currentEquipment.serialNumber
        val newDocId = newEquipment.id
        val newSerialNumber = newEquipment.serialNumber
        val businessDocRef = businessesRef.document(businessId)

        businessDocRef.collection(USERS_ENDPOINT)
            .document(firebaseId)
            .update(equipmentKey, newSerialNumber)
            .addOnSuccessListener {
                Log.e(TAG, "Updated $equipmentKey to $newSerialNumber")
            }.addOnFailureListener {
                Log.e(TAG, "Failed to update $equipmentKey to $newSerialNumber")
            }

        businessDocRef
            .collection(equipmentEndpoint)
            .document(currentDocId).update(IS_AVAILABLE_KEY, true)
                .addOnSuccessListener {
                    Log.e(TAG, "Set $equipmentEndpoint: $currentSerialNumber to available")
                }.addOnFailureListener {
                    Log.e(TAG, "Failed to set $equipmentEndpoint: $currentSerialNumber to available")
                }

        businessDocRef
            .collection(equipmentEndpoint)
            .document(newDocId).update(IS_AVAILABLE_KEY, false)
                .addOnSuccessListener {
                    Log.e(TAG, "Set $equipmentEndpoint: $newSerialNumber to unavailable")
                    trySend(Response.Success(null))
                }.addOnFailureListener { failure ->
                    Log.e(TAG, "Failed to set $equipmentEndpoint: $newSerialNumber to unavailable")
                    trySend(Response.Error(failure.message.orEmpty()))
                }.addOnCompleteListener {
                    close()
                }
        awaitClose {
            cancel()
        }
    }
}