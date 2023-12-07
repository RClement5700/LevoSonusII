package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.Department
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface DepartmentsRepository {
    fun fetchDepartmentsData(userInfo: LSUserInfo): Flow<Response<List<Department>>>
    fun subtractOrderPickerFromDepartment(userInfo: LSUserInfo)
    fun addOrderPickerToDepartment(userInfo: LSUserInfo, currentDepartmentId: String)
}