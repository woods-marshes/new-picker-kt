package io.github.woods_marshes.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.woods_marshes.base.builder.Ordering
import io.github.woods_marshes.base.builder.PickerKt
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.base.builder.query.operand.ContentColumn
import io.github.woods_marshes.base.builder.query.operand.valueOf
import io.github.woods_marshes.base.collection.CollectionRepository
import io.github.woods_marshes.base.content.ContentRepository
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.content.model.shouldSeparateApartFrom
import io.github.woods_marshes.base.contentresolver.AllFoldersCollection
import io.github.woods_marshes.base.contentresolver.ContentResolverColumn
import io.github.woods_marshes.base.contentresolver.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel(assistedFactory = ContentViewModel.Factory::class)
class ContentViewModel @AssistedInject constructor(
    private val contentRepository: ContentRepository,
    private val collectionRepository: CollectionRepository,
    @Assisted val collectionId: String,
    @Assisted config: PickerKtConfiguration
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(collectionId: String, config: PickerKtConfiguration): ContentViewModel
    }

    val collectionFlow = collectionRepository.CollectionFlow(
        coroutineScope = viewModelScope,
        config = PickerKt.picker {
            allowMimes {
                addAll(config.mimeTypes)
            }
        },
        collectionId = collectionId.toLongOrNull()
    )

    val contentListFlow = contentRepository.contentListFlow(
        coroutineScope = viewModelScope,
        config = PickerKt.picker {
            allowMimes {
                addAll(config.mimeTypes)
            }
            orderBy {
                add {
                    Ordering(
                        column = ContentResolverColumn.DateAdded,
                        order = Order.Descending
                    )
                }
            }
            predicate {
                if (collectionId != AllFoldersCollection.id) {
                    ContentColumn(column = ContentResolverColumn.CollectionId) equal valueOf(collectionId)
                }
                ContentColumn(column = ContentResolverColumn.ByteSize) greaterThan valueOf(0)
            }
        }
    )
        .flowOn(Dispatchers.Default)
        .cachedIn(scope = viewModelScope)

    val contentListTimeGroupedFlow = contentListFlow.mapLatest {
        it.insertSeparators { c1, c2 ->
            if (c1 == null && c2 != null) {
                return@insertSeparators LazyListItem.TimeGroupHeader(time = c2.dateAdded)
            }

            return@insertSeparators if (c1 != null && c2 != null) {
                if (c1 shouldSeparateApartFrom c2) LazyListItem.TimeGroupHeader(time = c2.dateAdded) else null
            } else {
                null
            }
        }
            .map { x ->
                when (x) {
                    is LazyListItem.TimeGroupHeader -> x
                    else -> LazyListItem.Data(x as Content)
                }
            }
    }
        .flowOn(Dispatchers.Default)
        .cachedIn(scope = viewModelScope)
}


sealed class LazyListItem<T> {

    class Data<T>(val data: T) : LazyListItem<T>()

    class TimeGroupHeader(val time: Instant) : LazyListItem<Nothing>()

}