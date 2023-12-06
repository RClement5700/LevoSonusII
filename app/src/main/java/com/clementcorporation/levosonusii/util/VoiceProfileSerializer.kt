package com.clementcorporation.levosonusii.util

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object VoiceProfileSerializer: Serializer<VoiceProfile> {
    override val defaultValue: VoiceProfile
        get() = VoiceProfile()

    override suspend fun readFrom(input: InputStream): VoiceProfile {
        return try {
            Json.decodeFromString(
                deserializer = VoiceProfile.serializer(),
                string = input.readBytes().decodeToString()
            )
        }catch(e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: VoiceProfile, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = VoiceProfile.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}