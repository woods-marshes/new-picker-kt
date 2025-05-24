package io.github.woods_marshes.ui.common.compose.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable

@Composable
fun AnimatedAppBarIconVisibility(visible: Boolean, icon: @Composable AnimatedVisibilityScope.() -> Unit) = AnimatedVisibility(
    visible = visible,
    enter = slideInVertically { -it } + fadeIn(),
    exit = slideOutVertically { -it } + fadeOut(),
    content = icon
)