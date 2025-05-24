package io.github.woods_marshes.base.content.datasource

import android.database.Cursor
import android.provider.MediaStore
import io.github.woods_marshes.base.common.unit.Byte
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.BUCKET_ID
import io.github.woods_marshes.base.contentresolver.MimeType
import kotlinx.datetime.Instant

internal val CONTENT_LOADER_PROJECTION = arrayOf(
    MediaStore.MediaColumns._ID,
    MediaStore.MediaColumns.DISPLAY_NAME,
    MediaStore.MediaColumns.DATE_ADDED,
    MediaStore.MediaColumns.MIME_TYPE,
    MediaStore.MediaColumns.SIZE,
    BUCKET_ID
)

internal fun Cursor.parseResolverRow(): Content {
    val id = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
    val displayName = getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
    val dateAdded = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED))
    val mimeType = getString(getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
    val byteSize = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
    val bucketId = getString(getColumnIndexOrThrow(BUCKET_ID))

    return Content(
        id = id,
        name = displayName,
        dateAdded = Instant.fromEpochMilliseconds(dateAdded),
        mimeType = MimeType.of(mimeType),
        size = Byte(byteSize),
        collectionId = bucketId
    )
}