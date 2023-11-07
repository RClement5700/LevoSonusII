package com.clementcorporation.levosonusii.data

import com.clementcorporation.levosonusii.screens.departments.Department
import com.clementcorporation.levosonusii.util.Resource
import kotlinx.coroutines.flow.Flow

interface DepartmentsRepository {
    fun fetchDepartmentsData(): Flow<Resource<List<Department>>>
    suspend fun subtractOrderPickerFromDepartment()
    suspend fun addOrderPickerToDepartment(currentDepartmentId: String)
}