package com.clementcorporation.levosonusii.domain.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.Boolean

data class EquipmentDto(
    val serialNumber: String = "",
    val isAvailable: Boolean = true,
    val connectionType: String? = null,
    val machineType: String? = null
)

fun EquipmentDto.toEquipmentUiModel(): EquipmentUiModel =
    EquipmentUiModel(
        connectionType = connectionType?.toConnectionType(),
        machineType = machineType?.toMachineType(),
        serialNumber = serialNumber,
        isAvailable = isAvailable,
        isSelected = mutableStateOf(false)
    )

data class EquipmentUiModel(
    val connectionType: ConnectionType? = null,
    val machineType: MachineType? = null,
    val serialNumber: String,
    val isAvailable: Boolean,
    val isSelected: MutableState<Boolean> = mutableStateOf(false),
)

fun String.toConnectionType() =
    when (this.lowercase()) {
        ConnectionType.WIRED.name.lowercase() -> ConnectionType.WIRED
        ConnectionType.BLUETOOTH.name.lowercase() -> ConnectionType.BLUETOOTH
        else -> null
    }

fun String.toMachineType() =
    when (this.lowercase()) {
        MachineType.ElectricPalletJack.name.lowercase() -> MachineType.ElectricPalletJack
        MachineType.Forklift.name.lowercase() -> MachineType.Forklift
        else -> null
    }

enum class ConnectionType {
    WIRED,
    BLUETOOTH
}

enum class MachineType(name: String) {
    ElectricPalletJack("Electric Pallet Jack"),
    Forklift("Forklift")
}