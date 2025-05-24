package io.github.woods_marshes.ui.picker.nav

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.woods_marshes.base.collection.model.Collection
import io.github.woods_marshes.base.collection.model.finalName
import io.github.woods_marshes.base.common.data.localizedName
import io.github.woods_marshes.ui.component.collection.CountBadge
import io.github.woods_marshes.ui.component.nav.DrawerGroupLabelItem
import io.github.woods_marshes.ui.component.nav.DrawerMenuItem
import io.github.woods_marshes.ui.layout.PickerAppDrawerState
import io.github.woods_marshes.ui.picker.nav.data.NavLocation
import io.github.woods_marshes.ui.picker.nav.data.preset.PresetNavLocation
import io.github.woods_marshes.base.common.data.Result
import io.github.woods_marshes.base.common.data.data
import io.github.woods_marshes.ui.component.nav.DrawerCollectionItem
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    drawerState: PickerAppDrawerState,
    collections: Result<List<Collection>>,
    selectionInCollectionMap: Map<String, Int>,
    currentRoute: String,
    presetNavLocations: List<PresetNavLocation>,
    onClick: (NavLocation) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp, start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        item {
            Spacer(modifier = Modifier.windowInsetsStartWidth(WindowInsets.statusBars))
        }

        item {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Media Picker", style = MaterialTheme.typography.headlineSmall)

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
                ) {
                    AnimatedContent(targetState = drawerState.isOpen, transitionSpec = { scaleIn() togetherWith scaleOut() }) {
                        Icon(
                            if (it) Icons.AutoMirrored.Filled.MenuOpen else Icons.Default.Menu,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        presetNavLocations.groupBy { it.group }.entries.forEachIndexed { i, (t, u) ->
            item {
                DrawerGroupLabelItem(title = t.localizedName, showDivider = i != 0)
            }
            u.forEach {
                item {
                    DrawerMenuItem(
                        title = it.localizedName,
                        leadingIcon = {
                            Icon(
                                it.icon,
                                contentDescription = null
                            )
                        },
                        onClick = { onClick(NavLocation.Preset(it)) },
                        selected = it.navHostRoute == currentRoute,
                        trailingIcon = if (it == PresetNavLocation.AllFolders || it == PresetNavLocation.Download) {
                            {
                                val count = when (it) {
                                    PresetNavLocation.AllFolders -> selectionInCollectionMap.toList()
                                        .foldRight(0) { p, n -> p.second + n }
                                    PresetNavLocation.Download -> selectionInCollectionMap[it.correspondingCollection!!.id]
                                        ?: 0
                                    else -> 0
                                }
                                CountBadge(
                                    count = count,
                                    contentDescription = "Folder named \"${it.localizedName}\" has $count items selected"
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }

        item {
            DrawerGroupLabelItem(title = "Folders", showDivider = true)
        }

        repeat(if (collections is Result.Loading) 10 else 0) {
            item {
                DrawerCollectionItem(
                    enabled = false,
                    onClick = {},
                    name = null,
                    info = null,
                    imageUri = null,
                    selected = false,
                    trailingIcon = {},
                    loading = true
                )
            }
        }

        collections.data?.let { list ->
            itemsIndexed(list) { _, it ->
                DrawerCollectionItem(
                    onClick = { onClick(NavLocation.Collection(it)) },
                    name = it.finalName,
                    info = "${it.contentCount} • ${it.relativeTimeString}",
                    imageUri = it.lastContentItem?.uri,
                    selected = currentRoute.substringAfter("/").contains(it.id),
                    trailingIcon = {
                        val count = selectionInCollectionMap[it.id]
                        CountBadge(
                            count,
                            contentDescription = "Folder named \"${it.finalName}\" has $count items selected"
                        )
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.windowInsetsStartWidth(WindowInsets.statusBars))
        }
    }
}