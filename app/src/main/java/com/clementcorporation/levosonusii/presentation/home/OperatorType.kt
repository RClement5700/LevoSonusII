package com.clementcorporation.levosonusii.presentation.home

import androidx.compose.runtime.MutableState

data class OperatorType(val title: String, val icon: Int, val isSelected: MutableState<Boolean>)