package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.DepartmentUiModel
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface DepartmentsRepository {
    fun fetchDepartmentsData(businessId: String): Flow<Response<List<DepartmentUiModel>>>
    fun subtractOrderPickerFromDepartment()
    fun addOrderPickerToDepartment()
}