package io.github.woods_marshes.ui.component.filter

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.ui.common.data.extension.icon
import io.github.woods_marshes.ui.component.Chip
import io.github.woods_marshes.ui.component.ChipDefaults

@Composable
fun MimeCollectionFilterBar(
    modifier: Modifier = Modifier,
    mimeTypeMap: Map<MimeType, Int>,
    selectedMimeSet: Set<MimeType>,
    onClick: (MimeType) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = {},
    ) {
        MimeCollectionChipGroups(
            mimeTypeMap = mimeTypeMap,
            selectedMimeSet = selectedMimeSet,
            onClick = onClick
        )
    }
}

@Composable
fun MimeCollectionChipGroups(
    modifier: Modifier = Modifier,
    mimeTypeMap: Map<MimeType, Int>,
    selectedMimeSet: Set<MimeType>,
    onClick: (MimeType) -> Unit
) {
    LazyRow(
        modifier = modifier
            .padding(vertical = 16.dp)
            .alpha(LocalContentColor.current.alpha),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(
            space = ChipDefaults.ChipGroupHorizontalSpacing,
            alignment = Alignment.Start
        )
    ) {
        items(mimeTypeMap.entries.sortedByDescending { it.value }, key = { it.key }) {
            Chip(
                selected = it.key in selectedMimeSet,
                leadingIcon = {
                    AnimatedContent(
                        targetState = it.key !in selectedMimeSet,
                        transitionSpec = {
                            fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                        }
                    ) { visible ->
                        if (visible) {
                            Icon(
                                painterResource(it.key.icon),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = null
                            )
                        }
                    }
                },
                onClick = {
                    onClick(it.key)
                }
            ) {
                Text(text = "${it.value} ${it.key.displayName}")
            }
        }
    }
}