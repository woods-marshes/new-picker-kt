package io.github.woods_marshes.base.content.model

import android.content.ContentUris
import android.net.Uri
import android.os.Parcelable
import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.woods_marshes.base.common.property.Identifiable
import io.github.woods_marshes.base.common.serializer.ByteParceler
import io.github.woods_marshes.base.common.serializer.InstantSerializer
import io.github.woods_marshes.base.common.serializer.KotlinxInstantParceler
import io.github.woods_marshes.base.common.unit.Byte
import io.github.woods_marshes.base.contentresolver.MimeType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Content(
    override val id: Long,
    override val name: String,
    @Serializable(with = InstantSerializer::class)
    @TypeParceler<Instant, KotlinxInstantParceler>
    val dateAdded: Instant,
    val mimeType: MimeType,
    @TypeParceler<Byte, ByteParceler>
    val size: Byte,
    val collectionId: String
) : Identifiable<Long>, Comparable<Content>, Parcelable {
    @IgnoredOnParcel
    val type: MimeType.Group
        get() = mimeType.group

    @IgnoredOnParcel
    val uri: Uri
        get() = ContentUris.withAppendedId(type.toMediaStoreExternalUri(), id)

    @IgnoredOnParcel
    val relativeTimeString: String
        @Composable get() {
            return DateUtils.getRelativeTimeSpanString(LocalContext.current, dateAdded.toEpochMilliseconds(), true).toString()
        }

    override fun compareTo(other: Content): Int {
        return id.compareTo(other.id)
    }

}

fun List<Content>.groupByCollectionAndCount(): Map<String, Int> =
    groupBy { it.collectionId }.mapValues { it.value.size }

fun List<Content>.groupByMimeTypeAndCount(): Map<MimeType, Int> =
    groupBy { it.mimeType }.mapValues { it.value.size }

fun List<Content>.groupByMimeTypeGroupAndCount(): Map<MimeType.Group, Int> =
    groupBy { it.mimeType.group }.mapValues { it.value.size }

fun Map<Long, Pair<Instant, Content>>.timeSortedValues(): List<Content> = values
    .sortedBy { it.first }
    .map { it.second }

fun List<Pair<Instant, Content>>.timeSortedValues(): List<Content> = this
    .sortedBy { it.first }
    .map { it.second }

infix fun Content.shouldSeparateApartFrom(content: Content): Boolean {
    val systemTimeZone: TimeZone = TimeZone.currentSystemDefault()
    val localDateTime1: LocalDateTime = this.dateAdded.toLocalDateTime(systemTimeZone)
    val localDateTime2: LocalDateTime = content.dateAdded.toLocalDateTime(systemTimeZone)
    val localDate1: LocalDate = localDateTime1.date
    val localDate2: LocalDate = localDateTime2.date
    return localDate1 != localDate2
}
