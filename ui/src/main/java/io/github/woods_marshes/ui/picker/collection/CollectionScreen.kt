package io.github.woods_marshes.ui.picker.collection

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import io.github.woods_marshes.base.ContentViewModel
import io.github.woods_marshes.base.LazyListItem
import io.github.woods_marshes.base.collection.model.Collection
import io.github.woods_marshes.base.collection.model.finalName
import io.github.woods_marshes.base.common.data.collectAsResultState
import io.github.woods_marshes.base.common.data.Result
import io.github.woods_marshes.base.common.data.data
import io.github.woods_marshes.base.common.data.defaultIfNull
import io.github.woods_marshes.base.common.unit.formatAsHumanReadableString
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.base.contentresolver.PredefinedCollectionIdSet
import io.github.woods_marshes.base.contentresolver.asCollection
import io.github.woods_marshes.base.contentresolver.getPredefinedCollectionById
import io.github.woods_marshes.ui.R
import io.github.woods_marshes.ui.common.compose.animation.AnimatedAppBarIconVisibility
import io.github.woods_marshes.ui.common.compose.data.MenuItem
import io.github.woods_marshes.ui.common.compose.data.appBarMenuItemOf
import io.github.woods_marshes.ui.common.compose.data.randomizeStringForPlaceholder
import io.github.woods_marshes.ui.common.compose.shimmerPlaceholder
import io.github.woods_marshes.ui.component.common.Placeholder
import io.github.woods_marshes.ui.component.content.MainContentPickerGrid
import io.github.woods_marshes.ui.component.filter.MimeCollectionFilterBar
import io.github.woods_marshes.ui.layout.PickerAppDrawerState.Companion.allowModalDrawer
import io.github.woods_marshes.ui.selection.ContentSelectionController

@Composable
fun CollectionScreen(
    modifier: Modifier = Modifier,
    showNavUpIcon: Boolean = false,
    onNavIconClick: () -> Unit,
    onItemClick: (Content) -> Unit,
    viewModel: ContentViewModel,
    selectionController: ContentSelectionController
) {
    val mimeFilterSet = remember { mutableStateMapOf<MimeType, Unit>() }
    val collection by viewModel.collectionFlow.collectAsResultState()
    val contents = viewModel.contentListTimeGroupedFlow.collectAsLazyPagingItems()

    CollectionScreen(
        modifier = modifier,
        showNavUpIcon = showNavUpIcon,
        onNavIconClick = onNavIconClick,
        collection = collection.let {
            if (viewModel.collectionId in PredefinedCollectionIdSet) {
                it.defaultIfNull { getPredefinedCollectionById(viewModel.collectionId).asCollection() }
            } else {
                it
            }
        },
        contents = contents,
        selectionMap = selectionController.canonicalSelectionOrderMap,
        menuItems = listOf(
            appBarMenuItemOf(
                name = "Select All",
                nameRes = null,
                subtitle = "",
                onClick = {
                    selectionController.select(
                        if (mimeFilterSet.isNotEmpty()) {
                            mimeFilterSet.keys.let { set ->
                                contents.itemSnapshotList.items
                                    .filterIsInstance<LazyListItem.Data<Content>>()
                                    .map { it.data }
                                    .filter { it.mimeType in set }
                            }
                        } else {
                            contents.itemSnapshotList.items
                                .filterIsInstance<LazyListItem.Data<Content>>()
                                .map { it.data }
                        }
                    )
                },
                composable = {
                    Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                }
            ),
            appBarMenuItemOf(
                name = "Remove All of the selection",
                nameRes = null,
                subtitle = "",
                onClick = {
                    // TODO: Improve the unselection behavior!
                    val idOfRenderedContents = contents.itemSnapshotList.items
                        .filterIsInstance<LazyListItem.Data<Content>>()
                        .map { it.data.id }
                        .toSet()
                    if (mimeFilterSet.isEmpty()) {
                        selectionController.removeIf { it.id in idOfRenderedContents }
                    } else {
                        mimeFilterSet.keys.let { set ->
                            selectionController.removeIf {
                                it.mimeType in set && it.id in idOfRenderedContents
                            }
                        }
                    }
                },
                composable = {
                    Icon(
                        painterResource(R.drawable.ui_ic_select_remove),
                        contentDescription = "Remove Selection"
                    )
                }
            )
        ),
        onItemSelect = { selectionController.select(it) },
        onItemUnselect = { selectionController.unselect(it) },
        onItemClick = onItemClick,
        mimeFilterSet = mimeFilterSet.keys,
        onMimeFilterClick = {
            if (it !in mimeFilterSet) {
                mimeFilterSet.putIfAbsent(it, Unit)
            } else {
                mimeFilterSet.remove(it)
            }
        }
    )
}


@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalStdlibApi::class,
    ExperimentalAnimationApi::class
)
fun CollectionScreen(
    modifier: Modifier = Modifier,
    onNavIconClick: () -> Unit,
    showNavUpIcon: Boolean = false,
    mimeFilterSet: Set<MimeType>,
    onMimeFilterClick: (MimeType) -> Unit,
    collection: Result<Collection>,
    contents: LazyPagingItems<LazyListItem<out Content>>,
    selectionMap: Map<Long, Int>,
    menuItems: List<MenuItem>,
    onItemClick: (Content) -> Unit,
    onItemSelect: (Content) -> Unit,
    onItemUnselect: (Content) -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val currentWindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val shouldDisplayMenuIcon = !currentWindowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val scrolledEnoughForSubtitle = scrollBehavior.state.heightOffset > scrollBehavior.state.heightOffsetLimit / 2
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = collection,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) {
            val navIcon = @Composable {
                if (showNavUpIcon) {
                    IconButton(
                        onClick = { onNavIconClick() },
                        content = {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null)
                        }
                    )
                } else {
                    AnimatedAppBarIconVisibility(shouldDisplayMenuIcon) {
                        IconButton(
                            onClick = { onNavIconClick() },
                            content = {
                                Icon(Icons.Default.Menu, contentDescription = null)
                            }
                        )
                    }
                }
            }

            if (it == Result.Loading || it is Result.Success) {
                LargeTopAppBar(
                    title = {
                        val hidden = LocalContentColor.current.alpha <= 0.51f
                        if (!hidden) {
                            Column {
                                when (collection) {
                                    Result.Loading,
                                    is Result.Success -> {
                                        Text(
                                            text = collection.data?.name
                                                ?: randomizeStringForPlaceholder(),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .padding(end = 16.dp)
                                                .fillMaxWidth(
                                                    if ((currentWindowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND))) 0.7f else 1f
                                                )
                                                .shimmerPlaceholder(visible = collection.data == null),
                                            style = LocalTextStyle.current
                                        )

                                        AnimatedVisibility(visible = scrolledEnoughForSubtitle) {
                                            Text(
                                                text = collection.data?.let {
                                                    buildList {
                                                        add("Total ${it.contentCount} items")
                                                        add(it.size.formatAsHumanReadableString())
                                                        add(it.relativeTimeString)
                                                    }.joinToString(" • ")
                                                } ?: randomizeStringForPlaceholder(),
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier
                                                    .padding(top = 2.dp)
                                                    .shimmerPlaceholder(visible = collection.data == null)
                                            )
                                        }
                                    }
                                    is Result.Error -> {}
                                }
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = navIcon,
                    actions = { menuItems.forEach { it.Composable() } }
                )
            } else if (it is Result.Error && allowModalDrawer) {
                TopAppBar(
                    title = {},
                    navigationIcon = navIcon
                )
            }
        }

        collection.data?.contentGroupCounts?.let { map ->
            MimeCollectionFilterBar(
                mimeTypeMap = map,
                selectedMimeSet = mimeFilterSet,
                onClick = { onMimeFilterClick(it) }
            )
        }

        val viewState = CollectionScreenViewState.rememberCollectionScreenViewState(
            contents.itemCount,
            contents.loadState.refresh
        )

        AnimatedContent(targetState = viewState) {
            when (it) {
                CollectionScreenViewState.Content -> {
                    MainContentPickerGrid(
                        state = rememberSaveable(saver = LazyGridState.Saver) {
                            LazyGridState()
                        },
                        selectionVisible = true,
                        content = contents,
                        scrollConnection = scrollBehavior.nestedScrollConnection,
                        selectionMap = selectionMap,
                        mimeFilterSet = mimeFilterSet,
                        onContentItemClick = { x -> onItemClick(x) }
                    ) { x ->
                        if (x.id in selectionMap) {
                            onItemUnselect(x)
                        } else {
                            onItemSelect(x)
                        }
                    }
                }
                CollectionScreenViewState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Placeholder(
                            title = { Text("Failed to get ${collection.data?.finalName}") },
                            subtitle = { Text("Add some images so that it will show here.") }
                        ) {
                            Icon(
                                Icons.Outlined.Image,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = CircleShape
                                    )
                                    .padding(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                CollectionScreenViewState.RefreshError -> {
                    Text(text = "Error")
                }
                CollectionScreenViewState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}