package io.github.woods_marshes.ui.picker.collection

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.LoadState

enum class CollectionScreenViewState {
    Content,
    Empty,
    RefreshError,
    Loading;

    companion object {
        fun determineState(
            itemCount: Int,
            pagingRefreshState: LoadState
        ): CollectionScreenViewState = when(pagingRefreshState) {
            LoadState.Loading -> Loading
            is LoadState.NotLoading -> if (itemCount == 0) Loading else Content
            is LoadState.Error -> if (pagingRefreshState.error is Resources.NotFoundException) {
                Empty
            } else {
                RefreshError
            }
        }

        @Composable
        fun rememberCollectionScreenViewState(
            itemCount: Int,
            pagingRefreshState: LoadState
        ): CollectionScreenViewState {
            var viewState by remember { mutableStateOf(Content) }
            LaunchedEffect(itemCount, pagingRefreshState) {
                viewState = determineState(itemCount = itemCount, pagingRefreshState = pagingRefreshState)
            }

            return viewState
        }
    }
}