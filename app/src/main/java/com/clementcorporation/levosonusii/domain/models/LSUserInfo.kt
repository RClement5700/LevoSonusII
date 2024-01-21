package com.clementcorporation.levosonusii.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class LSUserInfo(
    val organization: Business = Business(),
    val employeeId: String = "",
    val firebaseId: String = "",
    val name: String = "",
    val emailAddress: String = "",
    val password: String = "",
    val profilePicUrl: String = "",
    val machineId: String = "",
    val scannerId: String = "",
    val headsetId: String = "",
    val departmentId: String = "",
    val operatorType: String = "",
    val messengerIds: ArrayList<String> = arrayListOf(),
    val voiceProfile: Map<String, List<String>> = hashMapOf()
)

@Serializable
data class LSUserDto(
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
    val messengerIds: ArrayList<String> = arrayListOf(),
    val voiceProfile: Map<String, List<String>> = hashMapOf()
)

fun LSUserInfo.toDto():LSUserDto =
    LSUserDto(
        employeeId = employeeId,
        firebaseId = firebaseId,
        name = name,
        emailAddress = emailAddress,
        profilePicUrl = profilePicUrl,
        machineId = machineId,
        scannerId = scannerId,
        headsetId = headsetId,
        departmentId = departmentId,
        operatorType = operatorType,
        voiceProfile = voiceProfile,
        messengerIds = messengerIds
    )

fun LSUserDto.toMap(): MutableMap<String, Any> {
    return mutableMapOf(
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
        "voiceProfile" to voiceProfile
    )
}

@Serializable
data class Business(val id: String = "", val name: String = "", val address: String = "")
