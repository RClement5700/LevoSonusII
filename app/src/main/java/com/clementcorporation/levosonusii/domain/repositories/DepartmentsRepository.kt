package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.util.Resource
import kotlinx.coroutines.flow.Flow

interface DepartmentsRepository {
    fun fetchDepartmentsData(): Flow<Resource<List<com.clementcorporation.levosonusii.presentation.departments.Department>>>
    suspend fun subtractOrderPickerFromDepartment()
    suspend fun addOrderPickerToDepartment(currentDepartmentId: String)
}