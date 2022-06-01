package com.clementcorporation.levosonusii.model

data class LevoSonusUser(
    val userId: String,
    val name: String = "",
    val emailAddress: String,
    var equipmentId: String = "",
    var departmentId: String = "",
    var voiceProfileId: String = ""
) {
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "user_id" to userId,
            "name" to name,
            "emailAddress" to emailAddress,
            "equipmentId" to equipmentId,
            "departmentId" to departmentId,
            "voiceProfileId" to voiceProfileId
        )
    }
}