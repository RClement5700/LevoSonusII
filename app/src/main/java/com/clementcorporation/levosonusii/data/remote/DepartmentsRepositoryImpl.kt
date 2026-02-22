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
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DepartmentsRepositoryImpl @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val db: FirebaseFirestore
): DepartmentsRepository {

    override suspend fun fetchDepartmentsData(businessId: String): Flow<Response<List<DepartmentUiModel>>> =
        callbackFlow {
            db.collection(BUSINESSES_ENDPOINT)
                .document(businessId)
                .collection(DEPARTMENTS_ENDPOINT).get()
                .addOnSuccessListener { result ->
                    val departmentDtos = result.toObjects(DepartmentDto::class.java)
                    val departments = departmentDtos.map { it.toDepartmentUiModel() }.sortedBy { it.title }
                    if (departments.isNotEmpty()) Response.Success(departments)
                    else trySend(Response.Error("Failed to retrieve departments data"))
                }.addOnFailureListener { failure ->
                    trySend(Response.Error(failure.message.orEmpty()))
                }
        }



    override suspend fun subtractOperatorFromDepartment(
        departmentId: String,
        businessId: String,
        isOrderPicker: Boolean
    ): Flow<Response<String>> {
        val departmentsRef = db.collection(BUSINESSES_ENDPOINT)
            .document(businessId)
            .collection(DEPARTMENTS_ENDPOINT)
            .document(departmentId)
        return departmentsRef.snapshots().map { snapshot ->
            val department = snapshot.toObject(DepartmentDto::class.java)
            if (departmentsRef.update(
                    if (isOrderPicker) ORDER_PICKERS_PARAM
                    else FORKLIFTS_PARAM, FieldValue.increment(-1.0)).isSuccessful
                )
                Response.Success("${department?.title} updated successfully")
            else Response.Error("${department?.title} failed to update...")
        }
    }

    override suspend fun addOperatorToDepartment(
        departmentId: String,
        businessId: String,
        isOrderPicker: Boolean
    ): Flow<Response<String>> {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
        firebaseUserId?.let { firebaseId ->
            db.collection(BUSINESSES_ENDPOINT).document(businessId)
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
            val departmentsRef = db.collection(BUSINESSES_ENDPOINT)
                .document(businessId)
                .collection(DEPARTMENTS_ENDPOINT)
                .document(departmentId)
            return departmentsRef.snapshots().map { snapshot ->
                val department = snapshot.toObject(DepartmentDto::class.java)
                if (departmentsRef.update(
                        if (isOrderPicker) ORDER_PICKERS_PARAM
                        else FORKLIFTS_PARAM, FieldValue.increment(1.0)).isSuccessful
                    )
                    Response.Success("${department?.title} updated successfully")
                else Response.Error("${department?.title} failed to update...")
            }
        }
        return flowOf(Response.Error("Failed to find Firebase User Id"))
    }
}