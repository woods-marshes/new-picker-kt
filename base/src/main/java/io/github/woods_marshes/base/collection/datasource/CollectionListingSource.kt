package io.github.woods_marshes.base.collection.datasource

import android.content.Context
import android.content.res.Resources
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.base.builder.query.operand.ContentColumn
import io.github.woods_marshes.base.builder.query.operand.valueOf
import io.github.woods_marshes.base.common.data.datasource.ListingSource
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.base.collection.model.Collection
import io.github.woods_marshes.base.contentresolver.BUCKET_ID
import io.github.woods_marshes.base.utils.forEachIndexed
import io.github.woods_marshes.base.common.data.Result
import io.github.woods_marshes.base.common.data.transform
import io.github.woods_marshes.base.common.unit.Byte
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.AllFoldersCollection
import io.github.woods_marshes.base.contentresolver.ContentResolverColumn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.EnumMap

class CollectionListingSource(
    coroutineScope: CoroutineScope,
    private val context: Context,
    private val uri: Uri,
    private val selection: String,
    private val selectionArguments: Array<String>?,
    private val sortOrder: String?
) : ListingSource<Collection>() {

    constructor(
        coroutineScope: CoroutineScope,
        context: Context,
        config: PickerKtConfiguration
    ) : this(
        coroutineScope = coroutineScope,
        context = context,
        uri = MimeType.Group.Unknown.toMediaStoreExternalUri(),
//        selection = "",
        selection = config.predicateString ?: "",
//        selectionArguments = null,
        selectionArguments = config.predicateArgumentString,
//        sortOrder = null
        sortOrder = config.orderByString
    )

    init {
        coroutineScope.launch {
            context.contentResolver.registerContentObserver(
                uri,
                true,
                object : ContentObserver(Handler(Looper.myLooper()!!)) {
                    override fun onChange(selfChange: Boolean) {
                        refresh()
                    }
                }
            )
        }
    }

    override suspend fun fetchData(): Result<List<Collection>> {
        val queryCursor = context.contentResolver.query(
            uri,
            COLLECTION_LOADER_PROJECTION,
            selection,
            selectionArguments,
            sortOrder,
        ) ?: return Result.Error(throwable = IllegalStateException("Empty result"), data = null)

        if (queryCursor.count == 0) {
            return Result.Success(data = listOf())
        }

        return try {
            val listOfCollections = queryCursor.parseQueryCursor().toMutableList()

            // If no BUCKET_ID specified, display contents from any folder.
            if (!selection.contains(BUCKET_ID)) {
                listOfCollections.formulateTheAllFoldersCollection()?.let {
                    listOfCollections.add(0, it)
                }
            }

            Result.Success(data = listOfCollections)
        } catch (e: Exception) {
            Result.Error(throwable = e, data = null)
        } finally {
            queryCursor.close()
        }
    }

    private fun Cursor.parseQueryCursor(): List<Collection> {
        val map = mutableMapOf<String, Pair<Int, Collection>>()

        forEachIndexed {
            val (contentRow, tempCollection) = parseResolverRow()
            val collectionId = tempCollection.id

            map.putIfAbsent(collectionId, (map.size + 1) to tempCollection)
            map[collectionId] = map[collectionId]!!.copy(
                second = map[collectionId]!!.second.let { accumulatorCollection ->
                    accumulatorCollection.copy(
                        size = accumulatorCollection.size + contentRow.size,
                        contentCount = accumulatorCollection.contentCount + 1
                    ).apply {
                        contentGroupCounts as EnumMap<MimeType, Int>
                        contentGroupCounts.also {
                            tempCollection.contentGroupCounts?.forEach { (t, _) ->
                                it[t] = (it[t] ?: 0) + 1
                            }
                        }
                    }
                }
            )
        }

        return map.values.sortedBy { it.first }.map { it.second }
    }

    private fun List<Collection>.formulateTheAllFoldersCollection(): Collection? {
        var totalByteSize = Byte(0L)
        var totalContentCount = 0
        var theLastContentItem: Content? = null
        val mimeGroupMap = mutableMapOf<MimeType, Int>()

        forEach { collection ->
            totalByteSize += collection.size
            totalContentCount += collection.contentCount

            collection.contentGroupCounts?.forEach { (mimeGroup, count) ->
                mimeGroupMap[mimeGroup] = (mimeGroupMap[mimeGroup] ?: 0) + count
            }

            if (theLastContentItem == null || theLastContentItem!!.dateAdded.toEpochMilliseconds() < collection.lastContentItem?.dateAdded?.toEpochMilliseconds() ?: 0) {
                theLastContentItem = collection.lastContentItem
            }
        }

        val allTimeLastContentItem = theLastContentItem ?: return null

        return Collection(
            id = AllFoldersCollection.id,
            name = context.getString(AllFoldersCollection.nameStringRes),
            timeAdded = allTimeLastContentItem.dateAdded,
            size = totalByteSize,
            contentGroupCounts = mimeGroupMap,
            contentCount = totalContentCount,
            lastContentItem = allTimeLastContentItem
        )
    }
}



