package com.clementcorporation.levosonusii.util

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object LSUserInfoSerializer: Serializer<LSUserInfo> {
    override val defaultValue: LSUserInfo
        get() = LSUserInfo()

    override suspend fun readFrom(input: InputStream): LSUserInfo {
        return try {
            Json.decodeFromString(
                deserializer = LSUserInfo.serializer(),
                string = input.readBytes().decodeToString()
            )
        }catch(e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: LSUserInfo, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = LSUserInfo.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}