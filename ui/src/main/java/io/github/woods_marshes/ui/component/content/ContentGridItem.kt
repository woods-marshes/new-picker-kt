package io.github.woods_marshes.ui.component.content

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.ui.R
import io.github.woods_marshes.ui.common.data.extension.icon
import io.github.woods_marshes.ui.component.common.CheckMarkButton
import io.github.woods_marshes.ui.component.content.ContentGridItemDefaults.BottomEndGradientBackdrop
import io.github.woods_marshes.ui.component.content.ContentGridItemDefaults.CheckIconEnterAnimation
import io.github.woods_marshes.ui.component.content.ContentGridItemDefaults.CheckIconExitAnimation
import io.github.woods_marshes.ui.component.content.ContentGridItemDefaults.TopStartGradientBackdrop

object ContentGridItemDefaults {

    private val MimeTypeIconGradient = listOf(
        Color.Black.copy(alpha = 0.25f),
        Color.Transparent
    )

    val CheckIconEnterAnimation = slideIn { IntOffset(-it.width / 2, -it.height / 2) } + fadeIn()
    val CheckIconExitAnimation = slideOut { IntOffset(-it.width / 2, -it.height / 2) } + fadeOut()

    val TopStartGradientBackdrop = Brush.linearGradient(
        colors = MimeTypeIconGradient,
        start = Offset(0f, 0f),
        end = Offset(0f, 90f)
    )

    val BottomEndGradientBackdrop = Brush.radialGradient(
        colors = MimeTypeIconGradient,
        center = Offset(x = Float.POSITIVE_INFINITY, y = Float.POSITIVE_INFINITY),
        radius = Float.POSITIVE_INFINITY
    )
}

@Composable
fun ContentGridItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectionIndex: Int = -1,
    selected: Boolean = selectionIndex != -1,
    editModeActivated: Boolean,
    onClick: () -> Unit,
    onCheckClick: () -> Unit,
    badge: @Composable BoxScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {

    val selectionTransition = updateTransition(targetState = selected, label = "Selection")

    val backgroundAlpha by selectionTransition.animateFloat(label = "Background Alpha") {
        if (it) 0.05f else 0f
    }

    val padding by selectionTransition.animateDp(label = "Padding") {
        if (it) 16.dp else 0.dp
    }

    val cornerRadius by selectionTransition.animateDp(label = "Corner Radius") {
        if (it) 16.dp else 0.dp
    }

    val boxOpacity by animateFloatAsState(targetValue = if (enabled) 1f else 0.4f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(boxOpacity)
            .aspectRatio(1f)
            .clipToBounds()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = backgroundAlpha))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(padding)
                .clip(RoundedCornerShape(cornerRadius))
                .clickable(enabled) {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {

            content()

            AnimatedVisibility(
                visible = editModeActivated,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TopStartGradientBackdrop)
                )
            }
        }

        AnimatedVisibility(
            visible = editModeActivated,
            enter = CheckIconEnterAnimation,
            exit = CheckIconExitAnimation,
            modifier = Modifier.align(Alignment.TopStart)
        ) {

            val checkOpacity by animateFloatAsState(
                targetValue = if (enabled || selected) 1f else boxOpacity
            )

            CheckMarkButton(
                modifier = Modifier.alpha(checkOpacity),
                selected = selected,
                label = ((selectionIndex.takeIf { it >= 0 } ?: selectionIndex) + 1).toString(),
                borderColor = Color.White
            ) {
                onCheckClick()
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomEnd),
            content = badge
        )
    }
}

@Composable
fun CoilContentGridItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    editModeActivated: Boolean = false,
    selectionIndex: Int,
    selected: Boolean = selectionIndex >= 0,
    content: Content,
    onClick: () -> Unit,
    onCheckClick: () -> Unit,
) {
    CoilContentGridItem(
        modifier = modifier,
        enabled = enabled,
        editModeActivated = editModeActivated,
        selectionIndex = selectionIndex,
        selected = selected,
        onClick = onClick,
        imageUri = content.uri,
        contentDescription = null,
        onCheckClick = onCheckClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .background(BottomEndGradientBackdrop)
        )
        Icon(
            painterResource(content.mimeType.icon),
            contentDescription = content.mimeType.displayName,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun CoilContentGridItem(
    modifier: Modifier = Modifier,
    imageUri: Uri,
    contentDescription: String?,
    enabled: Boolean = true,
    editModeActivated: Boolean = false,
    selectionIndex: Int,
    selected: Boolean = selectionIndex >= 0,
    onClick: () -> Unit,
    onCheckClick: () -> Unit,
    badge: @Composable BoxScope.() -> Unit = {}
) {
    val context = LocalContext.current
    ContentGridItem(
        modifier = modifier,
        enabled = enabled,
        selected = selected,
        selectionIndex = selectionIndex,
        editModeActivated = if (!selected) {
            editModeActivated
        } else {
            true
        },
        onClick = onClick,
        onCheckClick = onCheckClick,
        badge = badge
    ) {
        CoilImage(
            imageRequest = {
                ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .memoryCacheKey(imageUri.toString())
                    .diskCacheKey(imageUri.toString())
                    .allowHardware(true)
                    .build()
            },
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                contentDescription = contentDescription,
            ),
            modifier = Modifier.fillMaxSize(),
            component = rememberImageComponent {
                +ShimmerPlugin(
                    Shimmer.Resonate(
                        baseColor = Color.White,
                        highlightColor = Color.LightGray,
                    )
                )
                +PlaceholderPlugin.Failure(R.drawable.ui_ic_alert_circle_outline)
            }
        )
    }
}