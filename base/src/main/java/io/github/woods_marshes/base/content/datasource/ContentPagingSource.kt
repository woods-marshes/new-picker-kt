package io.github.woods_marshes.base.content.datasource

import android.content.Context
import android.content.res.Resources
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.base.utils.pagedMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContentPagingSource(
    coroutineScope: CoroutineScope,
    private val context: Context,
    private val uri: Uri,
    private val selection: String,
    private val selectionArguments: Array<String>?,
    private val sortOrder: String?,
    private val resultCountLimit: Int?
) : PagingSource<Int, Content>() {

    constructor(
        coroutineScope: CoroutineScope,
        context: Context,
        config: PickerKtConfiguration
    ) : this(
        coroutineScope = coroutineScope,
        context = context,
        uri = MimeType.Group.Unknown.toMediaStoreExternalUri(),
        selection = config.predicateString ?: "",
        selectionArguments = config.predicateArgumentString,
        sortOrder = config.orderByString,
        resultCountLimit = config.selection.maxSelection
    )

    companion object {
        private const val INITIAL_REFRESH_KEY = 0
    }

    init {
        coroutineScope.launch {
            context.contentResolver.registerContentObserver(
                uri,
                true,
                object : ContentObserver(Handler(Looper.myLooper()!!)) {
                    override fun onChange(selfChange: Boolean) {
                        invalidate()
                    }
                }
            )
        }
        registerInvalidatedCallback {

        }
    }

    override val keyReuseSupported: Boolean
        get() = false

    override fun getRefreshKey(state: PagingState<Int, Content>): Int {
        return 0
    }

    private var remainingItemToBeTaken = resultCountLimit

    // private val idMap = mutableMapOf<String, Int>()
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Content> {

//        Timber.d(
//            t = null,
//            message = "Selection: \"$selection\", args: ${selectionArguments?.toList()}, order: \"$sortOrder\""
//        )

        return withContext(Dispatchers.IO) {
            context.contentResolver.query(
                uri,
                CONTENT_LOADER_PROJECTION,
                selection,
                selectionArguments,
                sortOrder,
            )
        }?.use { cursor ->
            try {
                val nextOffsetNumber = params.key ?: INITIAL_REFRESH_KEY

                val page = withContext(Dispatchers.Unconfined) {
                    cursor.pagedMap(offset = nextOffsetNumber, limit = params.loadSize) {
                        it.parseResolverRow()
                    }
                }

                if (nextOffsetNumber == INITIAL_REFRESH_KEY && page.isEmpty()) {
                    return LoadResult.Error(Resources.NotFoundException("There is no result for the specified criteria."))
                }

                if (remainingItemToBeTaken != null) {
                    remainingItemToBeTaken = remainingItemToBeTaken!! - page.size
                }

                val (data, nextKey) = remainingItemToBeTaken.let {
                    if (it != null && page.size > it) {
                        page.take(it.coerceAtLeast(resultCountLimit ?: 1)) to null
                    } else {
                        page to (if (page.isNotEmpty()) nextOffsetNumber + page.size else null)
                    }
                }

                LoadResult.Page(
                    data = data,
                    prevKey = null,
                    nextKey = nextKey,
                )
            } catch (e: Exception) {
                // Timber log removed
                LoadResult.Error(throwable = e)
            }

        } ?: LoadResult.Error(throwable = IllegalStateException("Empty result"))
    }
}