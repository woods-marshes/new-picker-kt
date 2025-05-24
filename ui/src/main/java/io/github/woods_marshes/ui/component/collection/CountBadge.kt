package io.github.woods_marshes.ui.component.collection

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.woods_marshes.ui.common.compose.animation.slideContentTransitionSpec

@Composable
fun CountBadge(
    count: Int?,
    modifier: Modifier = Modifier,
    visible: Boolean = count != null && count > 0,
    icon: (@Composable () -> Unit)? = {
        Icon(
            Icons.Default.CheckCircleOutline,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    },
    contentDescription: String?
) {
    AnimatedVisibility(
        modifier = modifier.animateContentSize(),
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Badge(
            modifier = Modifier.semantics(mergeDescendants = true) {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            }
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier.padding(4.dp).size(12.dp),
                    content = { icon() }
                )
            }

            AnimatedContent(
                targetState = count ?: 0,
                transitionSpec = slideContentTransitionSpec()
            ) {
                Text(
                    text = it.toString(),
                    modifier = Modifier.padding(start = if (icon != null) 0.dp else 4.dp ,end = 4.dp),
                    color = LocalContentColor.current,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}