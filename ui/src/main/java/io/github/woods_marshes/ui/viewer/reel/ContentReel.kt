package io.github.woods_marshes.ui.viewer.reel

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.ui.common.data.extension.icon
import io.github.woods_marshes.ui.selection.ContentSelectionController

@Composable
fun ContentReel(
    modifier: Modifier = Modifier,
    state: LazyListState,
    currentContent: Content?,
    contents: LazyPagingItems<Content>,
    selectionController: ContentSelectionController,
    onClick: (index: Int) -> Unit
) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    LazyRow(
        state = state,
        modifier = modifier.height(FocusedReelItemHeight),
        flingBehavior = rememberSnapFlingBehavior(
            lazyListState = state
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(horizontal = screenWidth / 2)
    ) {

        items(
            count = contents.itemCount,
            key = contents.itemKey{ it.id }
        ) { index ->
            val item = contents[index]
            item ?: return@items
            val isCurrentContent by rememberUpdatedState(newValue = currentContent?.id == item.id)

            ContentReelItem(
                selectionIndex = selectionController.canonicalSelectionList.indexOfFirst { x -> item.id == x.id },
                highlighted = isCurrentContent,
                onClick = { onClick(index) }
            ) {
                when (item.mimeType.group) {
                    MimeType.Group.Image -> CoilImage(
                        imageRequest = {
                            ImageRequest.Builder(context)
                                .data(item.uri)
                                .crossfade(true)
                                .build()
                        },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            contentDescription = null
                        ),
                        modifier = Modifier.fillMaxSize(),
                        component = rememberImageComponent {
                            +ShimmerPlugin(
                                Shimmer.Resonate(
                                    baseColor = Color.White,
                                    highlightColor = Color.LightGray,
                                )
                            )
                            +PlaceholderPlugin.Failure(Icons.Outlined.Folder)
                        }
                    )
                    else -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(item.mimeType.group.icon),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}