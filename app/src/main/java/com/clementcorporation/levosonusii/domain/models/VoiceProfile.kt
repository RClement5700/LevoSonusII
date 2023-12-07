package com.clementcorporation.levosonusii.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class VoiceProfile(val voiceProfileMap: HashMap<String, ArrayList<String>> = hashMapOf())