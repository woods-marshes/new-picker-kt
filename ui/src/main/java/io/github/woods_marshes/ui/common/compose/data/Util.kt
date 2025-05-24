package io.github.woods_marshes.ui.common.compose.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.woods_marshes.ui.common.randomizeWhiteSpaces

@Composable
fun randomizeStringForPlaceholder(lengthRange: IntRange = 10..30): String = remember {
    randomizeWhiteSpaces(lengthRange = lengthRange)
}