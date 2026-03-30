package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.EquipmentUiModel
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface EquipmentRepository {
    fun getHeadsets(businessId: String, equipmentId: String): Flow<Response<List<EquipmentUiModel>>>
    fun getScanners(businessId: String, equipmentId: String): Flow<Response<List<EquipmentUiModel>>>
    fun getMachines(businessId: String, equipmentId: String): Flow<Response<List<EquipmentUiModel>>>
}