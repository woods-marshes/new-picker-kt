package io.github.woods_marshes.ui.common.compose

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

inline fun <T : Any> LazyGridScope.itemsIndexed(
    items: LazyPagingItems<T>,
    noinline span: (LazyGridItemSpanScope.(item: T) -> GridItemSpan)? = null,
    crossinline itemContent: @Composable (LazyGridItemScope.(index: Int, value: T?) -> Unit)
) {
    items(
        count = items.itemCount,
        span = if (span != null) {
            {
                span(items[it]!!)
            }
        } else {
            null
        }
    ) { index ->
        itemContent(index, items[index])
    }
}