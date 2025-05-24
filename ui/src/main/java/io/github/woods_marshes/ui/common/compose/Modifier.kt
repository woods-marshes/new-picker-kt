package io.github.woods_marshes.ui.common.compose

import android.R.attr.direction
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline

fun Modifier.shimmerPlaceholder(
    visible: Boolean,
    shape: Shape = RoundedCornerShape(8.dp),
    color: Color? = null
): Modifier = composed {
    if (!visible) return@composed this

    val baseColor = color ?: LocalContentColor.current.copy(
        alpha = if (isSystemInDarkTheme()) 0.1F else 0.2F
    )

    val shimmerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
        alpha = if (isSystemInDarkTheme()) 0.6F else 0.3F
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    drawWithCache {
        val brush = Brush.linearGradient(
            colors = listOf(
                baseColor,
                shimmerColor,
                baseColor
            ),
            start = Offset(
                x = -size.width * 0.8f + progress * size.width * 2,
                y = -size.height * 0.8f + progress * size.height * 2
            ),
            end = Offset(
                x = 0f + progress * size.width * 2,
                y = 0f + progress * size.height * 2
            )
        )

        onDrawWithContent {
            drawPath(
                path = Path().apply {
                    addOutline(
                        shape.createOutline(
                            size = size,
                            layoutDirection = layoutDirection,
                            density = this@drawWithCache
                        )
                    )
                },
                brush = brush
            )
        }
    }
}