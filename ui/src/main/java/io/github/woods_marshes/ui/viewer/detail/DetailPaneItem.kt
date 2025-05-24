package io.github.woods_marshes.ui.viewer.detail

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.woods_marshes.ui.common.compose.data.randomizeStringForPlaceholder
import io.github.woods_marshes.ui.common.compose.shimmerPlaceholder

@Composable
fun DetailPaneItemPlaceholder(
    modifier: Modifier = Modifier
) {

    val loading = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    val placeholderIcon = @Composable {
        Box(modifier = Modifier.size(24.dp).shimmerPlaceholder(loading.currentState))
    }

    DetailPaneItem(
        modifier = modifier,
        onClick = { /*TODO*/ },
        icon = { placeholderIcon() },
        title = { Text(randomizeStringForPlaceholder(), modifier = Modifier.shimmerPlaceholder(loading.currentState)) },
        subtitle = { Text(randomizeStringForPlaceholder(), modifier = Modifier.shimmerPlaceholder(loading.currentState)) },
        trailingIcon = { placeholderIcon() }
    )
}

@Composable
fun DetailPaneItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(color),
    icon: (@Composable BoxScope.() -> Unit)? = null,
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable BoxScope.() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = enabled,
        color = color,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (icon != null) {
                Box(
                    modifier = Modifier,
                    content = icon
                )
            }

            Column(
                modifier = Modifier
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.titleMedium) {
                    title()
                }

                ProvideTextStyle(
                    value = MaterialTheme.typography.bodySmall.copy(
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                ) {
                    subtitle?.invoke()
                }
            }

            if (trailingIcon != null) {
                Box(
                    modifier = Modifier,
                    content = trailingIcon
                )
            }
        }
    }
}