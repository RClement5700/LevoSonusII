package com.clementcorporation.levosonusii.data.remote

import com.clementcorporation.levosonusii.domain.models.DepartmentDto
import com.clementcorporation.levosonusii.domain.models.DepartmentUiModel
import com.clementcorporation.levosonusii.domain.models.toDepartmentUiModel
import com.clementcorporation.levosonusii.domain.repositories.DepartmentsRepository
import com.clementcorporation.levosonusii.util.Constants.BUSINESSES_ENDPOINT
import com.clementcorporation.levosonusii.util.Constants.DEPARTMENTS_ENDPOINT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DepartmentsRepositoryImpl : DepartmentsRepository {

    override fun fetchDepartmentsData(businessId: String): Flow<Response<List<DepartmentUiModel>>> =
        FirebaseFirestore.getInstance().collection(BUSINESSES_ENDPOINT)
            .document(businessId)
            .collection(DEPARTMENTS_ENDPOINT)
            .snapshots().map { snapshot ->
                val departmentDtos = snapshot.toObjects(DepartmentDto::class.java)
                val departments = departmentDtos.map { it.toDepartmentUiModel() }.sortedBy { it.id }
                if (departments.isNotEmpty()) Response.Success(departments)
                else Response.Error("Failed to retrieve departments data")
            }

    override fun subtractOrderPickerFromDepartment() {

    }

    override fun addOrderPickerToDepartment() {

    }
}