package io.github.woods_marshes.ui.component.collection

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import io.github.woods_marshes.base.collection.model.Collection
import io.github.woods_marshes.base.common.unit.formatAsHumanReadableString
import io.github.woods_marshes.ui.component.list.MediumSizeListItem

@Composable
fun CollectionGridItem(
    modifier: Modifier = Modifier,
    collection: Collection,
    onLongClick: () -> Unit = {},
    compact: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    MediumSizeListItem(
        modifier = modifier,
        onClick = onClick,
        icon = {
            CoilImage(
                imageRequest = {
                    ImageRequest
                        .Builder(context)
                        .data(collection.lastContentItem?.uri)
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
        },
        compact = compact,
        title = collection.name,
        subtitle = collection.size.formatAsHumanReadableString()
    )
}