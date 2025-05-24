package io.github.woods_marshes.base.collection

import android.content.Context
import android.content.res.Resources
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.base.builder.query.operand.ContentColumn
import io.github.woods_marshes.base.builder.query.operand.valueOf
import io.github.woods_marshes.base.collection.datasource.CollectionListingSource
import io.github.woods_marshes.base.collection.model.Collection
import io.github.woods_marshes.base.common.data.Result
import io.github.woods_marshes.base.common.data.transform
import io.github.woods_marshes.base.contentresolver.ContentResolverColumn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CollectionRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    @Suppress("FunctionName")
    fun CollectionFlow(
        coroutineScope: CoroutineScope,
        collectionId: Long?,
        config: PickerKtConfiguration
    ): Flow<Result<Collection>> {
        val wildcardOnly = collectionId == null
        val modifiedConfig = config.asBuilder()
            .apply {
                if (!wildcardOnly) {
                    predicate {
                        ContentColumn(ContentResolverColumn.CollectionId) equal valueOf(collectionId!!)
                    }
                }
            }
            .build()

        return CollectionListingSource(
            coroutineScope = coroutineScope,
            context = context,
            config = modifiedConfig
        ).flow
            .map { result ->
                result.transform {
                    when {
                        it.size == 1 -> it.lastOrNull()
                            ?: throw Resources.NotFoundException()
                        wildcardOnly -> it.firstOrNull()
                            ?: throw Resources.NotFoundException()
                        else -> throw Resources.NotFoundException()
                    }

                }
            }
            .catch {
                emit(Result.Error(throwable = it, data = null))
            }
    }

    fun collectionListFlow(
        coroutineScope: CoroutineScope,
        config: PickerKtConfiguration
    ) = CollectionListingSource(
        coroutineScope = coroutineScope,
        context = context,
        config = config
    )
        .flow
}