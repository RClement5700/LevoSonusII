package com.clementcorporation.levosonusii.screens.equipment.model

sealed class Equipment {
    data class Headset(val type: String, val serialNumber: Int): Equipment()
    data class ElectricPalletJack(val serialNumber: String): Equipment()
    data class Forklift(val serialNumber: String): Equipment()
    data class ProductScanner(val type: String, val serialNumber: Int): Equipment()
}
