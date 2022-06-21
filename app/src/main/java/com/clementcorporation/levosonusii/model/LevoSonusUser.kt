package com.clementcorporation.levosonusii.model

data class LevoSonusUser(
    val userId: String,
    val name: String = "",
    val emailAddress: String,
    var equipmentId: String = "",
    var departmentId: String = "",
    var profilePicUrl: String = "",
    var voiceProfile: HashMap<String, ArrayList<String>> = hashMapOf()
) {
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "userId" to userId,
            "name" to name,
            "emailAddress" to emailAddress,
            "equipmentId" to equipmentId,
            "departmentId" to departmentId,
            "profilePicUrl" to profilePicUrl,
            "voiceProfile" to voiceProfile
        )
    }
}