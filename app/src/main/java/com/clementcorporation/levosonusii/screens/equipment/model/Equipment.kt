package com.clementcorporation.levosonusii.screens.equipment.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Equipment(val id: String) {
    data class Headset(val connection: String, val serialNumber: String, val isAvailable: Boolean,
                       val isSelected: MutableState<Boolean> = mutableStateOf(false)): Equipment(serialNumber)
    data class ElectricPalletJack(val serialNumber: String, val isAvailable: Boolean,
                                  val isSelected: MutableState<Boolean> = mutableStateOf(false)): Equipment(serialNumber)
    data class Forklift(val serialNumber: String, val isAvailable: Boolean,
                        val isSelected: MutableState<Boolean> = mutableStateOf(false)): Equipment(serialNumber)
    data class ProductScanner(val connection: String, val serialNumber: String, val isAvailable: Boolean,
                              val isSelected: MutableState<Boolean> = mutableStateOf(false)): Equipment(serialNumber)
}
