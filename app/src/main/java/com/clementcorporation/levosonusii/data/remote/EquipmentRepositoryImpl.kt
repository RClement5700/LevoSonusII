package com.clementcorporation.levosonusii.data.remote

import com.clementcorporation.levosonusii.domain.models.Equipment
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

class EquipmentRepositoryImpl: EquipmentRepository {
    override fun getHeadsets(): Flow<Response<List<Equipment.Headset>>> {
        TODO("Not yet implemented")
    }

    override fun getScanners(): Flow<Response<List<Equipment.ProductScanner>>> {
        TODO("Not yet implemented")
    }

    override fun getForklifts(): Flow<Response<List<Equipment.Forklift>>> {
        TODO("Not yet implemented")
    }

    override fun getElectricPalletJacks(): Flow<Response<List<Equipment.ElectricPalletJack>>> {
        TODO("Not yet implemented")
    }
}