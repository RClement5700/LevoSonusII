package com.clementcorporation.levosonusii.model

import kotlinx.serialization.Serializable
import java.util.*
import kotlin.collections.HashMap

@Serializable
data class VoiceProfile(val voiceProfileMap: HashMap<String, ArrayList<String>> = hashMapOf())