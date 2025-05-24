package io.github.woods_marshes.ui.picker.library

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.woods_marshes.base.builder.Ordering
import io.github.woods_marshes.base.builder.PickerKt
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.base.builder.query.operand.ContentColumn
import io.github.woods_marshes.base.builder.query.operand.valueOf
import io.github.woods_marshes.base.collection.CollectionRepository
import io.github.woods_marshes.base.content.ContentRepository
import io.github.woods_marshes.base.contentresolver.ContentResolverColumn
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.base.contentresolver.Order

@HiltViewModel(assistedFactory = LibraryViewModel.Factory::class)
class LibraryViewModel @AssistedInject constructor(
    @ApplicationContext val context: Context,
    private val contentRepository: ContentRepository,
    private val collectionRepository: CollectionRepository,
    @Assisted mimeTypeGroup: MimeType.Group?,
    @Assisted config: PickerKtConfiguration,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(mimeTypeGroup: MimeType.Group?, config: PickerKtConfiguration): LibraryViewModel
    }

    val collectionList = collectionRepository.collectionListFlow(
        coroutineScope = viewModelScope,
        config = PickerKt.picker {
            allowMimes {
                addAll(config.mimeTypes.filter { it.group == mimeTypeGroup })
            }
        }
    )

    val recentContentList = contentRepository.contentListFlow(
        coroutineScope = viewModelScope,
        config = PickerKt.picker {
            allowMimes {
                addAll(config.mimeTypes.filter { it.group == mimeTypeGroup })
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
                ContentColumn(column = ContentResolverColumn.ByteSize) greaterThan valueOf(0)
            }
        },
        pageSize = 20,
        enablePlaceholders = true
    ) .cachedIn(viewModelScope)
}