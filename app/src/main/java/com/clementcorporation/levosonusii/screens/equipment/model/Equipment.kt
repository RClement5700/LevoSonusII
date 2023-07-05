package com.clementcorporation.levosonusii.screens.equipment.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Equipment {
    data class Headset(val type: String, val serialNumber: String, val isAvailable: Boolean,
                       val isSelected: MutableState<Boolean> = mutableStateOf(false)): Equipment()
    data class ElectricPalletJack(val serialNumber: String, val isAvailable: Boolean,
                                  val isSelected: MutableState<Boolean> = mutableStateOf(false)): Equipment()
    data class Forklift(val serialNumber: String, val isAvailable: Boolean,
                        val isSelected: MutableState<Boolean> = mutableStateOf(false)): Equipment()
    data class ProductScanner(val type: String, val serialNumber: String, val isAvailable: Boolean,
                              val isSelected: MutableState<Boolean> = mutableStateOf(false)): Equipment()
}
