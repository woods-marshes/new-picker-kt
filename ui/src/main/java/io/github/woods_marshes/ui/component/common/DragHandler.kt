package io.github.woods_marshes.ui.component.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DragHandler(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        val color by animateColorAsState(
            targetValue = if (isSystemInDarkTheme()) {
                Color.White.copy(alpha = 0.4f)
            } else {
                Color.Black.copy(alpha = 0.3f)
            }
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .width(96.dp)
                .height(4.dp)
                .background(color = color, shape = CircleShape)
        )
    }
}