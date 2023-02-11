package de.mgprogramms.birthdayremindr.providers.presents

import Presents
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object PresentsSerializer : Serializer<Presents> {
    override val defaultValue: Presents = Presents.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Presents {
        try {
            return Presents.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Presents, output: OutputStream) =
        t.writeTo(output)
}