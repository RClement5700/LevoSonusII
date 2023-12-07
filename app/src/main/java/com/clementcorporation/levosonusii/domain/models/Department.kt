package com.clementcorporation.levosonusii.domain.models

import androidx.compose.runtime.MutableState

data class Department(
    val id: String,
    val title: String,
    val remainingOrders: String,
    val orderPickersCount: String,
    val forkliftCount: String,
    val iconUrl: String,
    var isSelected: MutableState<Boolean>
)