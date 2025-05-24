package io.github.woods_marshes.base.common.serializer

import android.os.Parcel
import androidx.annotation.Keep
import io.github.woods_marshes.base.common.unit.Byte
import kotlinx.parcelize.Parceler

@Keep
object ByteParceler : Parceler<Byte> {
    override fun create(parcel: Parcel): Byte {
        return Byte(value = parcel.readLong())
    }

    override fun Byte.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(this.value)
    }
}