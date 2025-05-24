package io.github.woods_marshes.ui.component.nav

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import io.github.woods_marshes.ui.R
import io.github.woods_marshes.ui.common.compose.data.randomizeStringForPlaceholder
import io.github.woods_marshes.ui.common.compose.shimmerPlaceholder


@Composable
fun DrawerCollectionItem(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit,
    trailingIcon: @Composable BoxScope.() -> Unit,
    name: String?,
    info: String?,
    imageUri: Uri?
) {
    val backgroundAlpha by animateFloatAsState(targetValue = if (selected) 1f else 0f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        onClick = onClick,
        enabled = enabled,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = backgroundAlpha),
        shape = RoundedCornerShape(16.dp)
    ) {
        val context = LocalContext.current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            CoilImage(
                imageRequest = {
                    coil3.request.ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCacheKey(imageUri.toString())
                        .build()
                },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    contentDescription = null
                ),
                modifier = Modifier
                    .padding(end = 24.dp, top = 12.dp, bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .size(56.dp),
                component = rememberImageComponent {
                    if (loading) {
                        +ShimmerPlugin(
                            Shimmer.Resonate(
                                baseColor = Color.White,
                                highlightColor = Color.LightGray,
                            )
                        )
                    }
                    +PlaceholderPlugin.Failure(R.drawable.ui_ic_alert_circle_outline)
                }
            )

            CompositionLocalProvider(LocalContentColor provides if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .animateContentSize()
                ) {
                    Text(
                        text = name ?: randomizeStringForPlaceholder(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.shimmerPlaceholder(loading)
                    )

                    Text(
                        text = info ?: randomizeStringForPlaceholder(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LocalContentColor.current.copy(
                                alpha = 0.6F
                            )
                        ),
                        modifier = Modifier.shimmerPlaceholder(loading)
                    )
                }
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier
                .padding(start = 8.dp)
                .animateContentSize()) {
                trailingIcon()
            }
        }
    }
}