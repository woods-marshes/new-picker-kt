package io.github.woods_marshes.base.common.serializer

import android.os.Parcel
import androidx.annotation.Keep
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parceler
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A Serializer for [kotlinx.serialization] that serializes and deserializes [Instant].
 */
@Keep
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochMilliseconds(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.toEpochMilliseconds())
    }
}

@Keep
object KotlinxInstantParceler : Parceler<Instant> {
    override fun create(parcel: Parcel): Instant {
        val epochMilli = parcel.readLong()
        return Instant.fromEpochMilliseconds(epochMilli)
    }

    override fun Instant.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(this.toEpochMilliseconds())
    }
}