package io.github.woods_marshes.base.contentresolver

import android.os.Build
import android.provider.MediaStore
import io.github.woods_marshes.base.R
import io.github.woods_marshes.base.collection.model.Collection
import io.github.woods_marshes.base.collection.model.CollectionBase
import io.github.woods_marshes.base.common.unit.Byte
import kotlinx.datetime.Clock

val BUCKET_ID: String
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.MediaColumns.BUCKET_ID else "bucket_id"

val BUCKET_DISPLAY_NAME: String
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.MediaColumns.BUCKET_DISPLAY_NAME else "bucket_display_name"

const val CONTENT_ID: String = MediaStore.MediaColumns._ID

/**
 * The value of BUCKET_ID of contents in "/storage/emulated/0/Download".
 * TODO: Find a more reliable way to detect the Download folder!
 */
const val PUBLIC_DOWNLOAD_FOLDER_BUCKET_ID = 540528482

const val WILDCARD_COLLECTION_ID = "WILDCARD"


object AllFoldersCollection : CollectionBase {
    override val id: String = WILDCARD_COLLECTION_ID
    override val name: String = "All Folders"
    override val nameStringRes: Int = R.string.base_collection_wildcard
}

object DownloadCollection : CollectionBase {
    override val id: String = PUBLIC_DOWNLOAD_FOLDER_BUCKET_ID.toString()
    override val name: String = "Download"
    override val nameStringRes: Int = R.string.base_collection_download
}

val PredefinedCollections = arrayOf(AllFoldersCollection, DownloadCollection)
val PredefinedCollectionIdSet = setOf(PUBLIC_DOWNLOAD_FOLDER_BUCKET_ID.toString(), WILDCARD_COLLECTION_ID)

val CollectionBase.isPredefined: Boolean
    get() = id in PredefinedCollectionIdSet

fun CollectionBase.asCollection() = Collection(
    id = id,
    name = name,
    contentCount = 0,
    timeAdded = Clock.System.now(),
    size = Byte(0),
    lastContentItem = null,
    contentGroupCounts = null
)

fun getPredefinedCollectionById(id: String) = PredefinedCollections.first { it.id == id }
