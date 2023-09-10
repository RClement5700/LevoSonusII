package com.clementcorporation.levosonusii.model

import kotlinx.serialization.Serializable

@Serializable
data class LSUserInfo(
    val organization: Organization = Organization(),
    val employeeId: String = "",
    val firebaseId: String = "",
    val name: String = "",
    val emailAddress: String = "",
    val profilePicUrl: String = "",
    val machineId: String = "",
    val scannerId: String = "",
    val headsetId: String = "",
    val departmentId: String = "",
    val operatorType: String = "",
    val messengerIds: ArrayList<String> = arrayListOf()
)

@Serializable
data class Organization(val id: String = "", val name: String = "", val address: String = "")
