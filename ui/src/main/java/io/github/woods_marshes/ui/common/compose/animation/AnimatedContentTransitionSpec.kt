package io.github.woods_marshes.ui.common.compose.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

fun <T : Comparable<T>> slideContentTransitionSpec(clip: Boolean = true): AnimatedContentTransitionScope<T>.() -> ContentTransform =
    slideContentTransitionSpec(clip = clip) { it }

@OptIn(ExperimentalAnimationApi::class)
fun <X, T : Comparable<T>> slideContentTransitionSpec(
    clip: Boolean = true,
    map: (X) -> T
): AnimatedContentTransitionScope<X>.() -> ContentTransform =
    {
        val initialValue = map(initialState)
        val targetValue = map(targetState)
        when {
            targetValue > initialValue -> {
                slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height } + fadeOut()
            }
            targetValue == initialValue -> {
                fadeIn() togetherWith fadeOut()
            }
            else -> {
                slideInVertically { height -> -height } + fadeIn() togetherWith
                        slideOutVertically { height -> height } + fadeOut()
            }
        }.using(SizeTransform(clip = clip))
    }