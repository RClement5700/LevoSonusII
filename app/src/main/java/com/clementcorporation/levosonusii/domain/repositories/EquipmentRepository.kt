package com.clementcorporation.levosonusii.domain.repositories

import com.clementcorporation.levosonusii.domain.models.Equipment
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow

interface EquipmentRepository {
    fun getHeadsets(): Flow<Response<List<Equipment.Headset>>>
    fun getScanners(): Flow<Response<List<Equipment.ProductScanner>>>
    fun getForklifts(): Flow<Response<List<Equipment.Forklift>>>
    fun getElectricPalletJacks(): Flow<Response<List<Equipment.ElectricPalletJack>>>
}