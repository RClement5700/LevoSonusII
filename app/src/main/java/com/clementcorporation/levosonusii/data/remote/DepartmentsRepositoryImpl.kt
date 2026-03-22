package com.clementcorporation.levosonusii.data.remote

import androidx.datastore.core.DataStore
import com.clementcorporation.levosonusii.domain.models.DepartmentDto
import com.clementcorporation.levosonusii.domain.models.DepartmentUiModel
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.toDepartmentUiModel
import com.clementcorporation.levosonusii.domain.repositories.DepartmentsRepository
import com.clementcorporation.levosonusii.util.Constants.BUSINESSES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.DEPARTMENTS_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.DEPARTMENT_ID
import com.clementcorporation.levosonusii.util.Constants.FORKLIFTS_PARAM
import com.clementcorporation.levosonusii.util.Constants.ORDER_PICKERS_PARAM
import com.clementcorporation.levosonusii.util.Constants.USERS_ENDPOINT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class DepartmentsRepositoryImpl @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val db: FirebaseFirestore
): DepartmentsRepository {

    val businessesRef = db.collection(BUSINESSES_ENDPOINT)

    override suspend fun fetchDepartmentsData(businessId: String): Flow<Response<List<DepartmentUiModel>>> =
        callbackFlow {
            if (businessId.isBlank()) {
                trySend(Response.Error("Invalid business ID"))
                close()
                return@callbackFlow
            }
            val businessDocRef = businessesRef.document(businessId)
            businessDocRef.collection(DEPARTMENTS_ENDPOINT).get()
                .addOnSuccessListener { result ->
                    val departmentDtos = result.toObjects(DepartmentDto::class.java)
                    val departments = departmentDtos.map { it.toDepartmentUiModel() }.sortedBy { it.title }
                    if (departments.isNotEmpty()) {
                        trySend(Response.Success(departments))
                    } else {
                        trySend(Response.Error("Failed to retrieve departments data"))
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

    override suspend fun subtractOperatorFromDepartment(
        departmentId: String,
        businessId: String,
        isOrderPicker: Boolean
    ): Flow<Response<String>> = callbackFlow {
        val departmentsRef = businessesRef
            .document(businessId)
            .collection(DEPARTMENTS_ENDPOINT)
            .document(departmentId)
        departmentsRef.update(
            if (isOrderPicker) ORDER_PICKERS_PARAM else FORKLIFTS_PARAM,
            FieldValue.increment(-1.0)
        ).addOnSuccessListener {
            trySend(Response.Success("Successfully updated department"))
        }.addOnFailureListener {
            trySend(Response.Error("Failed to update department"))
        }.addOnCompleteListener {
            close()
        }
        awaitClose {
            cancel()
        }
    }

    override suspend fun addOperatorToDepartment(
        departmentId: String,
        businessId: String,
        isOrderPicker: Boolean
    ): Flow<Response<String>> = callbackFlow {
        //TODO: Why doesn't this update the departmentId in Firebase?
        val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
        firebaseUserId?.let { firebaseId ->
            businessesRef.document(businessId)
                .collection(USERS_ENDPOINT)
                .document(firebaseId)
                .update(DEPARTMENT_ID, departmentId)
            sessionDataStore.updateData {
                it.copy(
                    organization = it.organization,
                    firebaseId = it.firebaseId,
                    employeeId = it.employeeId,
                    emailAddress = it.emailAddress,
                    password = it.password,
                    departmentId = departmentId,
                    machineId = it.machineId,
                    scannerId = it.scannerId,
                    headsetId = it.headsetId,
                    operatorType = it.operatorType,
                    voiceProfile = it.voiceProfile,
                    messengerIds = it.messengerIds
                )
            }
            val departmentsRef = businessesRef
                .document(businessId)
                .collection(DEPARTMENTS_ENDPOINT)
                .document(departmentId)
            departmentsRef.update(
                if (isOrderPicker) ORDER_PICKERS_PARAM else FORKLIFTS_PARAM,
                FieldValue.increment(1.0)
            ).addOnCompleteListener {
                close()
            }
        }
        awaitClose {
            cancel()
        }
    }
}