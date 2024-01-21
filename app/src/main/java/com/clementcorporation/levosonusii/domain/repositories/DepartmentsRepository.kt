package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.DepartmentUiModel
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface DepartmentsRepository {
    suspend fun fetchDepartmentsData(): Response<List<DepartmentUiModel>>?
    suspend fun subtractOrderPickerFromDepartment(): Response<String>?
    suspend fun addOrderPickerToDepartment(departmentId: String): Response<String>?
}