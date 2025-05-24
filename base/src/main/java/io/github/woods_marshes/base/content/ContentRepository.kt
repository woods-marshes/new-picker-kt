package io.github.woods_marshes.base.content

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.woods_marshes.base.builder.Ordering
import io.github.woods_marshes.base.builder.PickerKt
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.base.builder.query.operand.ContentColumn
import io.github.woods_marshes.base.builder.query.operand.valueOf
import io.github.woods_marshes.base.content.datasource.ContentPagingSource
import io.github.woods_marshes.base.content.datasource.ContentSingleSource
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.AllFoldersCollection
import io.github.woods_marshes.base.contentresolver.ContentResolverColumn
import io.github.woods_marshes.base.contentresolver.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContentRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    @Suppress("FunctionName")
    fun ContentSingleSource(
        coroutineScope: CoroutineScope,
        contentId: Long
    ): ContentSingleSource {
        return ContentSingleSource(
            coroutineScope = coroutineScope,
            context = context,
            config = PickerKt.picker {
                predicate {
                    ContentColumn(ContentResolverColumn.ContentId) equal valueOf(contentId)
                }
            }
        )
    }

    fun contentListFlow(
        coroutineScope: CoroutineScope,
        config: PickerKtConfiguration,
        pageSize: Int = 10,
        enablePlaceholders: Boolean = true,
    ): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = enablePlaceholders)
        ) {
            ContentPagingSource(
                coroutineScope = coroutineScope,
                context = context,
                config = config
            )
        }
            .flow
    }
}