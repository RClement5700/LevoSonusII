package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.EquipmentUiModel
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface EquipmentRepository {
    fun getEquipment(
        businessId: String,
        equipmentId: String,
        equipmentEndpoint: String
    ): Flow<Response<List<EquipmentUiModel>>>

    fun setEquipmentId(
        businessId: String,
        firebaseId: String,
        equipmentKey: String,
        newEquipment: EquipmentUiModel,
        currentEquipment: EquipmentUiModel,
        equipmentEndpoint: String
    ): Flow<Response<Boolean>>
}