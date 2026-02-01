package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.DepartmentUiModel
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface DepartmentsRepository {
    suspend fun fetchDepartmentsData(businessId: String): Flow<Response<List<DepartmentUiModel>>>
    suspend fun subtractOperatorFromDepartment(
        departmentId: String,
        businessId: String,
        isOrderPicker: Boolean
    ): Flow<Response<String>>
    suspend fun addOperatorToDepartment(
        departmentId: String,
        businessId: String,
        isOrderPicker: Boolean
    ): Flow<Response<String>>
}