package com.clementcorporation.levosonusii.screens.departments

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class Department(val id: String, val title: String, val remainingOrders: String, val orderPickersCount: String,
                      val forkliftCount: String, val iconUrl: String,
                      var isSelected: MutableState<Boolean> = mutableStateOf(false)
)