package io.github.woods_marshes.ui.picker.selectionmanager

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.HighlightAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.content.model.groupByMimeTypeAndCount
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.ui.R
import io.github.woods_marshes.ui.common.compose.LocalPickerConfig
import io.github.woods_marshes.ui.component.collection.CountBadge
import io.github.woods_marshes.ui.component.common.CircularHeroIconPlaceholder
import io.github.woods_marshes.ui.component.content.CoilContentGridItem
import io.github.woods_marshes.ui.component.filter.MimeCollectionFilterBar
import io.github.woods_marshes.ui.selection.ContentSelectionController
import io.github.woods_marshes.ui.selection.isNotEmpty
import io.github.woods_marshes.ui.selection.rememberContentSelectionController

@Composable
fun ContentSelectionModal(
    modifier: Modifier = Modifier,
    selectedContents: List<Content>,
    onCloseClick: (List<Content>) -> Unit,
    onConfirmed: (List<Content>) -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val tempSelectionController = rememberContentSelectionController(
        initialSelections = selectedContents,
        maxSelection = LocalPickerConfig.current.selection.maxSelection ?: Int.MAX_VALUE
    )

    ContentSelectionManagerGrid(
        modifier = modifier
            .padding(
                top = 36.dp,
                bottom = if (!windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) 0.dp else 36.dp,
            )
            .fillMaxWidth(fraction = if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) 0.6f else 1f)
            .clip(RoundedCornerShape(16.dp)),
        contents = selectedContents,
        temporarySelection = tempSelectionController,
        onCloseClick = onCloseClick,
        onConfirmed = onConfirmed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentSelectionManagerGrid(
    modifier: Modifier = Modifier,
    contents: List<Content>,
    temporarySelection: ContentSelectionController,
    onCloseClick: (List<Content>) -> Unit,
    onConfirmed: (List<Content>) -> Unit
) {
    val lazyListState = rememberLazyGridState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    BackHandler {
        // FIXME: This doesn't get called when back press.
        onConfirmed(temporarySelection.canonicalSelectionList)
    }

    val selectedMimes = remember { mutableStateMapOf<MimeType, Unit>() }
    val mimeTypeMap = contents.groupByMimeTypeAndCount()

    fun getSelectionForManipulation() = if (selectedMimes.isEmpty()) {
        contents
    } else {
        contents.filter { it.mimeType in selectedMimes }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.ui_selection_page_title)) },
                    navigationIcon = {
                        IconButton(
                            onClick = { onCloseClick(temporarySelection.canonicalSelectionList) }
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                getSelectionForManipulation().forEach {
                                    temporarySelection.select(it)
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.SelectAll,
                                contentDescription = stringResource(R.string.ui_selection_action_select_all_visible)
                            )
                        }

                        IconButton(
                            onClick = {
                                getSelectionForManipulation().forEach {
                                    temporarySelection.unselect(it)
                                }
                            }
                        ) {
                            Icon(
                                painterResource(id = R.drawable.ui_ic_select_remove),
                                contentDescription = stringResource(R.string.ui_selection_button_confirm)
                            )
                        }

                        IconButton(
                            onClick = {
                                getSelectionForManipulation().forEach {
                                    temporarySelection.toggleSelection(it)
                                }
                            }
                        ) {
                            Icon(
                                painterResource(id = R.drawable.ui_ic_select_inverse),
                                contentDescription = stringResource(R.string.ui_selection_action_invert_selection)
                            )
                        }
                    }
                )

                MimeCollectionFilterBar(
                    mimeTypeMap = mimeTypeMap,
                    selectedMimeSet = selectedMimes.keys,
                    onClick = {
                        if (it in selectedMimes) {
                            selectedMimes.remove(it)
                        } else {
                            selectedMimes.putIfAbsent(it, Unit)
                        }
                    }
                )
            }
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (LocalPickerConfig.current.selection.maxSelection != null) {
                            stringResource(
                                R.string.ui_selection_count_select_with_max,
                                temporarySelection.size,
                                LocalPickerConfig.current.selection.maxSelection!!
                            )
                        } else {
                            stringResource(R.string.ui_selection_count_select, temporarySelection.size)
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                    Button(
                        enabled = temporarySelection.isNotEmpty(),
                        modifier = Modifier.animateContentSize(),
                        onClick = { onConfirmed(temporarySelection.canonicalSelectionList) }
                    ) {

                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            modifier = Modifier.padding(end = if (temporarySelection.isNotEmpty()) 8.dp else 0.dp),
                            text = stringResource(R.string.ui_selection_button_confirm)
                        )

                        if (temporarySelection.size > 0) {
                            CountBadge(
                                count = temporarySelection.size,
                                contentDescription = null,
                                icon = null
                            )
                        }
                    }
                }
            }
        }
    ) {

        if (contents.isEmpty()) {
            CircularHeroIconPlaceholder(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                heroIcon = Icons.Outlined.HighlightAlt
            ) {
                Text(text = "You haven't select anything!")
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                state = lazyListState,
                contentPadding = it,
                columns = GridCells.Adaptive(120.dp),
            ) {
                itemsIndexed(contents) { _, c ->
                    CoilContentGridItem(
                        content = c,
                        editModeActivated = true,
                        selectionIndex = temporarySelection.canonicalSelectionList.indexOfFirst { x -> x.id == c.id },
                        onClick = { temporarySelection.toggleSelection(c) },
                        onCheckClick = { temporarySelection.toggleSelection(c) },
                        enabled = if (selectedMimes.isEmpty()) true else c.mimeType in selectedMimes
                    )
                }
            }
        }

    }
}