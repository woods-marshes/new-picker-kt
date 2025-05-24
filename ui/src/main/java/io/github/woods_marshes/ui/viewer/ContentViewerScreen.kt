package io.github.woods_marshes.ui.viewer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.woods_marshes.base.common.data.Result
import io.github.woods_marshes.base.common.data.data
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.ui.common.compose.animation.slideContentTransitionSpec
import io.github.woods_marshes.ui.component.common.CheckMark
import io.github.woods_marshes.ui.component.common.CheckMarkSize
import io.github.woods_marshes.ui.component.common.CircularHeroIconPlaceholder
import io.github.woods_marshes.ui.layout.ContentScope
import io.github.woods_marshes.ui.layout.SidePaneBottomSheetLayout
import io.github.woods_marshes.ui.selection.ContentSelectionController
import io.github.woods_marshes.ui.viewer.content.AudioContentViewer
import io.github.woods_marshes.ui.viewer.content.ImageContentViewer
import io.github.woods_marshes.ui.viewer.content.VideoContentViewer
import io.github.woods_marshes.ui.viewer.detail.DetailPane
import io.github.woods_marshes.ui.viewer.reel.ContentReel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.indexOfFirst
import kotlin.math.absoluteValue

object ContentPreviewScreenDefaults {
    val ControlVisibilityEnterTransition = fadeIn()
    val ControlVisibilityExitTransition = fadeOut()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentPreviewScreen(
    modifier: Modifier = Modifier,
    state: ContentPreviewState = rememberContentPreviewState(),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    foregroundColor: Color = LocalContentColor.current,
    title: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    info: @Composable (ContentScope.(asSidePane: Boolean) -> Unit),
    actions: @Composable (RowScope.() -> Unit),
    bottomBar: @Composable (RowScope.() -> Unit),
    navigationIcon: @Composable () -> Unit,
    body: @Composable () -> Unit
) {
    val pageContent = @Composable {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .background(backgroundColor)
            ) {
                CompositionLocalProvider(LocalContentColor provides foregroundColor) {
                    body()
                }
            }

            AnimatedVisibility(
                visible = state.controlVisible.value,
                enter = ContentPreviewScreenDefaults.ControlVisibilityEnterTransition,
                exit = ContentPreviewScreenDefaults.ControlVisibilityExitTransition
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            AnimatedVisibility(
                visible = state.controlVisible.value,
                enter = ContentPreviewScreenDefaults.ControlVisibilityEnterTransition,
                exit = ContentPreviewScreenDefaults.ControlVisibilityExitTransition
            ) {
                TopAppBar(
                    modifier = Modifier
                        .statusBarsPadding()
                        .align(Alignment.TopCenter),
                    title = title,
                    scrollBehavior = null,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        }
    }

    SidePaneBottomSheetLayout(
        modifier = modifier,
        state = state.infoState,
        paneContent = info
    ) {
        pageContent()

        AnimatedVisibility(
            visible = state.controlVisible.value,
            enter = ContentPreviewScreenDefaults.ControlVisibilityEnterTransition,
            exit = ContentPreviewScreenDefaults.ControlVisibilityExitTransition,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .height(256.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
                    .semantics {
                        testTag = "Bottom Gradient Overlay"
                    }
            )
        }

//        val sizePx = with(LocalDensity.current) { 50.dp.toPx() }
//        Box(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .fillMaxWidth()
//                .height(65.dp)
//                .swipeable(
//                    state = swipeableState,
//                    anchors = mapOf(0f to 0, sizePx to 1),
//                    orientation = Orientation.Vertical,
//                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
//                )
//                .semantics {
//                    testTag = "Bottom Handle for opening info drawer"
//                }
//        )
        AnimatedVisibility(
            visible = state.controlVisible.value,
            enter = ContentPreviewScreenDefaults.ControlVisibilityEnterTransition,
            exit = ContentPreviewScreenDefaults.ControlVisibilityExitTransition,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        floatingActionButton()
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .semantics {
                            testTag = "Bottom action bar"
                        },
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    content = bottomBar
                )
            }
        }
    }
}


@Composable
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
fun ContentPreviewScreenBody(
    modifier: Modifier = Modifier,
    collectionLazyPagingItems: LazyPagingItems<Content>,
    contentId: Long,
    selectionController: ContentSelectionController,
    onCurrentContentSelectionClick: (Content) -> Unit,
    onMainPreviewChange: (Content) -> Unit,
    onBackPress: () -> Unit
) {
    val state = rememberContentPreviewState()
    val coroutineScope = rememberCoroutineScope()
    
    val context = LocalContext.current
    
//    val contentPagingSnapshot = remember { mutableStateListOf<Content>() }
//    LaunchedEffect(
//        collectionLazyPagingItems.itemCount,
//        collectionLazyPagingItems.itemSnapshotList
//    ) {
//        contentPagingSnapshot.clear()
//        contentPagingSnapshot.addAll(
//            collectionLazyPagingItems
//                .itemSnapshotList
//                .mapNotNull { it }
//        )
//    }

    val pagerState = rememberPagerState(
        initialPage = collectionLazyPagingItems
            .itemSnapshotList
            .items
            .indexOfFirst { it.id == contentId }
            .takeIf { it >= 0 } ?: 0,
        pageCount = { collectionLazyPagingItems.itemCount }
    )

    LaunchedEffect(pagerState.targetPage) {
        val currentContent = collectionLazyPagingItems[pagerState.targetPage]
        if (currentContent != null) {
            onMainPreviewChange(currentContent)
        }
    }

    val lazyRowState = rememberLazyListState()

    var content by remember(pagerState.targetPage, collectionLazyPagingItems) {
        val currentContent = collectionLazyPagingItems[pagerState.targetPage]
        mutableStateOf<Result<Content>>(
            if (currentContent != null) Result.Success(currentContent)
            else Result.Loading
        )
    }

    LaunchedEffect(contentId, collectionLazyPagingItems) {
        val contentIndexOnPagingItems = collectionLazyPagingItems
            .itemSnapshotList
            .items
            .indexOfFirst { it.id == contentId }
            .takeIf { it >= 0 } ?: 0

        if (pagerState.currentPage != contentIndexOnPagingItems) {
            coroutineScope.launch {
                // Determine whether to animate or snap scroll based on distance
                if ((pagerState.currentPage - contentIndexOnPagingItems).absoluteValue <= 10) { // Arbitrary threshold for animation vs snap
                    pagerState.animateScrollToPage(contentIndexOnPagingItems)
                } else {
                    pagerState.scrollToPage(contentIndexOnPagingItems)
                }
            }
        }

        coroutineScope.launch {
            // Add a small delay to allow Pager to potentially load the item and for layout to happen
            delay(200)
            // Ensure the item index is valid before scrolling ContentReel
            if (contentIndexOnPagingItems < collectionLazyPagingItems.itemCount) {
                lazyRowState.animateScrollToItem(contentIndexOnPagingItems)
            }
        }
    }

    val controlVisible by state.controlVisible
    val backgroundColor by animateColorAsState(
        targetValue = if (controlVisible) MaterialTheme.colorScheme.background else Color.Black,
        label = "Background Color Animation"
    )

    val foregroundColor by animateColorAsState(
        targetValue = if (controlVisible) MaterialTheme.colorScheme.onBackground else Color.White,
        label = "Foreground Color Animation"
    )

//    val systemUiColor by animateFloatAsState(
//        targetValue = if (controlVisible) 0.5f else 0f
//    )

//    val systemUiController = rememberSystemUiController()
//    LaunchedEffect(systemUiColor, controlVisible) {
//        systemUiController.setSystemBarsColor(
//            color = Color.Black.copy(alpha = systemUiColor),
//            darkIcons = false
//        )
//        systemUiController.isStatusBarVisible = controlVisible
//    }

    ContentPreviewScreen(
        modifier = modifier,
        state = state,
        backgroundColor = backgroundColor,
        foregroundColor = foregroundColor,
        title = {
            AnimatedContent(
                targetState = content,
                transitionSpec = slideContentTransitionSpec { it.data?.id ?: 0 }
            ) {
                Text(text = it.data?.name ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentPagerContent = collectionLazyPagingItems.peek(pagerState.currentPage)
                SmallFloatingActionButton(
                    onClick = { state.toggleInfoSection() },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null)
                }

                if (currentPagerContent != null) {
                    ExtendedFloatingActionButton(
                        onClick = { onCurrentContentSelectionClick(currentPagerContent) },
                        text = {
                            Text(
                                text = if (selectionController.canonicalSelectionList.any { it.id == currentPagerContent.id })  "Unselect" else "Select",
                                modifier = Modifier.animateContentSize()
                            )
                        },
                        icon = {
                            val selectionIndex = selectionController.canonicalSelectionList.indexOfFirst { it.id == currentPagerContent.id }
                            val isSelected = selectionIndex >= 0
                            CheckMark(
                                label = (selectionIndex + 1).toString(),
                                selected = isSelected,
                                size = CheckMarkSize.Mini
                            )
                        }
                    )
                }
            }
        },
        info = {
            DetailPane(
                modifier = Modifier.systemBarsPadding(),
                content = content,
                sidePaneTopBarVisible = it,
                onCloseClick = { state.toggleInfoSection() }
            )
        },
        actions = {
            val currentPagerContent = collectionLazyPagingItems.peek(pagerState.currentPage)
            if (currentPagerContent != null) {
                IconButton(onClick = { onCurrentContentSelectionClick(currentPagerContent) }) {
                    val selectionIndex =
                        selectionController.canonicalSelectionList.indexOfFirst { it.id == contentId }
                    CheckMark(label = (selectionIndex + 1).toString(), selected = selectionIndex >= 0)
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ContentReel(
                    modifier = Modifier.weight(1f),
                    state = lazyRowState,
                    contents = collectionLazyPagingItems,
                    currentContent = content.data,
                    selectionController = selectionController,
                    onClick = {
                        coroutineScope.launch {
                            if ((pagerState.currentPage - it).absoluteValue <= 10) {
                                pagerState.animateScrollToPage(it)
                            } else {
                                pagerState.scrollToPage(it)
                            }
                        }
                    }
                )
            }
        },
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        HorizontalPager(
            beyondViewportPageCount = collectionLazyPagingItems.itemCount,
            state = pagerState,
            key = { index ->
                collectionLazyPagingItems.peek(index)?.id ?: index
            }
        ) { pageIndex ->
            val currentContent = collectionLazyPagingItems[pageIndex]
            if (currentContent == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (currentContent.mimeType.group) {
                    MimeType.Group.Image -> {
                        ImageContentViewer(
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds(),
                            coilImageRequest = ImageRequest.Builder(context)
                                .data(currentContent.uri)
                                .crossfade(true)
                                .build(),
                            onClick = {
                                state.toggleControlVisibility()
                            }
                        )
                    }
                    MimeType.Group.Video -> {
                        VideoContentViewer(
                            onClick = {
                                state.toggleControlVisibility()
                            },
                            title = {
                                Text(text = currentContent.name)
                            }
                        )
                    }
                    MimeType.Group.Audio -> {
                        AudioContentViewer(
                            onClick = {
                                state.toggleControlVisibility()
                            },
                            title = {
                                Text(text = currentContent.name)
                            }
                        )
                    }
                    else -> {
                        CircularHeroIconPlaceholder(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(indication = null, interactionSource = interactionSource) {
                                    state.toggleControlVisibility()
                                },
                            heroIcon = Icons.Outlined.Warning
                        ) {
                            Text(text = "Unknown file type, we cannot open it.")
                        }
                    }
                }
            }
        }
    }
}