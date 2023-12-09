package com.clementcorporation.levosonusii.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class LSUserInfo(
    val organization: Business = Business(),
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

fun LSUserInfo.toMap(): MutableMap<String, Any> {
    return mutableMapOf(
        "business" to organization,
        "employeeId" to employeeId,
        "name" to name,
        "emailAddress" to emailAddress,
        "headsetId" to headsetId,
        "scannerId" to scannerId,
        "machineId" to machineId,
        "departmentId" to departmentId,
        "operatorType" to operatorType,
        "messengerIds" to messengerIds,
        "profilePicUrl" to profilePicUrl,
    )
}

@Serializable
data class Business(val id: String = "", val name: String = "", val address: String = "")
