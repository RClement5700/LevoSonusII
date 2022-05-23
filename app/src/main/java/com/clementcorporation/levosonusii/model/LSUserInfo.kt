package com.clementcorporation.levosonusii.model

import kotlinx.serialization.Serializable

@Serializable
data class LSUserInfo(
    val employeeId: String = "",
    val name: String = "",
    val emailAddress: String = "",
    val equipmentId: String = "",
    val departmentId: String = "",
    val voiceProfileId: String = ""
)
