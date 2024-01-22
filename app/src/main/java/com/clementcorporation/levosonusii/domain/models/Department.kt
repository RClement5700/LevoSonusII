package com.clementcorporation.levosonusii.domain.models

import com.clementcorporation.levosonusii.R

data class DepartmentUiModel(
    val id: String,
    val title: String,
    val remainingOrders: String,
    val totalOrders: String,
    val orderPickers: Int,
    val forklifts: Int,
    val icon: Int,
)

data class DepartmentDto(
    val id: String = "",
    val title: String = "",
    val remainingOrders: String = "",
    val totalOrders: String = "",
    val orderPickers: Int = 0,
    val forklifts: Int = 0,
)

fun DepartmentDto.toDepartmentUiModel(): DepartmentUiModel =
    DepartmentUiModel(
        id = id,
        title = title,
        remainingOrders = remainingOrders,
        totalOrders = totalOrders,
        forklifts = forklifts,
        orderPickers = orderPickers,
        icon = when (title) {
            "Grocery" -> R.drawable.grocery_icon
            "Produce" -> R.drawable.produce_icon
            "Meat" -> R.drawable.meat_icon2
            "Seafood" -> R.drawable.seafood_icon
            "Dairy" -> R.drawable.dairy_icon
            "Freezer" -> R.drawable.freezer_icon
            else -> 0
        }
    )