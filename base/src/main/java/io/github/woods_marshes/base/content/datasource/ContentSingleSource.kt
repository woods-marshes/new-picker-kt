package io.github.woods_marshes.base.content.datasource

import android.content.Context
import android.content.res.Resources
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import io.github.woods_marshes.base.builder.PickerKt
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.base.builder.query.operand.ContentColumn
import io.github.woods_marshes.base.builder.query.operand.valueOf
import io.github.woods_marshes.base.common.data.datasource.SingleItemSource
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.base.common.data.Result
import io.github.woods_marshes.base.contentresolver.ContentResolverColumn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContentSingleSource(
    coroutineScope: CoroutineScope,
    private val context: Context,
    private val uri: Uri,
    private val selection: String,
    private val selectionArguments: Array<String>?
) : SingleItemSource<Content>() {

    internal constructor(
        coroutineScope: CoroutineScope,
        context: Context,
        config: PickerKtConfiguration
    ) : this(
        coroutineScope = coroutineScope,
        context = context,
        uri = MimeType.Group.Unknown.toMediaStoreExternalUri(),
        selection = config.predicateString ?: "",
        selectionArguments = config.predicateArgumentString
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

    override suspend fun fetchData(): Result<Content> {
        val queryCursor = withContext(Dispatchers.IO) {
            context.contentResolver.query(
                uri,
                CONTENT_LOADER_PROJECTION,
                selection,
                selectionArguments,
                null,
            )
        }

        if (queryCursor == null || queryCursor.count == 0) {
            return Result.Error(data = null, Resources.NotFoundException())
        }

        queryCursor.moveToFirst()
        val content = try {
            queryCursor.parseResolverRow()
        } catch (e: Exception) {
            return Result.Error(data = null, e)
        } finally {
            queryCursor.close()
        }

        return Result.Success(content)
    }
}