package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.EquipmentUiModel
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface EquipmentRepository {
    fun getHeadsets(businessId: String): Flow<Response<List<EquipmentUiModel>>>
    fun getScanners(businessId: String): Flow<Response<List<EquipmentUiModel>>>
    fun getMachines(businessId: String): Flow<Response<List<EquipmentUiModel>>>
}