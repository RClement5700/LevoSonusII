package com.clementcorporation.levosonusii.util

import kotlinx.serialization.Serializable

@Serializable
data class VoiceProfile(val voiceProfileMap: HashMap<String, ArrayList<String>> = hashMapOf())