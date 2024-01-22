package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.DepartmentUiModel
import com.clementcorporation.levosonusii.util.Response

interface DepartmentsRepository {
    suspend fun fetchDepartmentsData(): Response<List<DepartmentUiModel>>?
    suspend fun subtractOperatorFromDepartment(): Response<String>?
    suspend fun addOperatorToDepartment(departmentId: String): Response<String>?
}