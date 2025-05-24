package io.github.woods_marshes.ui.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass

class SidePaneBottomSheetLayoutState(
    initialVisibility: Boolean = false
) {
    private val _state = mutableStateOf(initialVisibility)
    val state: State<Boolean> = _state

    fun setValue(value: Boolean) {
        _state.value = value
    }

    fun toggle() {
        _state.value = !_state.value
    }

    fun open() {
        _state.value = true
    }

    fun close() {
        _state.value = false
    }
}

@Composable
fun rememberSidePaneBottomSheetLayoutState(initialVisibility: Boolean = false) = remember {
    SidePaneBottomSheetLayoutState(initialVisibility)
}

data class ContentScope constructor(
    val bottomSheetVisible: Boolean,
    val drawerVisible: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SidePaneBottomSheetLayout(
    modifier: Modifier = Modifier,
    state: SidePaneBottomSheetLayoutState,
    drawerWidth: Dp = 360.dp,
    paneContent: @Composable ContentScope.(asSidePane: Boolean) -> Unit,
    body: @Composable BoxScope.() -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val sidePanelVisible by state.state
    val useBottomSheet = !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val bottomSheetState = rememberModalBottomSheetState(
        confirmValueChange = {
            it != SheetValue.Hidden
        },
        skipPartiallyExpanded = true
    )

    LaunchedEffect(sidePanelVisible, useBottomSheet) {
        if (useBottomSheet) {
            if (sidePanelVisible) {
                bottomSheetState.show()
            } else {
                bottomSheetState.hide()
            }
        }
        if (!useBottomSheet && bottomSheetState.isVisible) {
            bottomSheetState.hide()
        }
    }

    LaunchedEffect(bottomSheetState.isVisible, useBottomSheet) {
        if (useBottomSheet) { // 仅在 Compact/Medium
            state.setValue(bottomSheetState.isVisible)
        }
    }

    val contentScope =
        remember(windowSizeClass, sidePanelVisible, bottomSheetState.isVisible) {
            ContentScope(
                bottomSheetVisible = useBottomSheet && bottomSheetState.isVisible,
                drawerVisible = !useBottomSheet && sidePanelVisible
            )
        }
    Box(modifier = modifier.fillMaxSize()) {
        if (useBottomSheet) {
            // Compact 和 Medium: 使用 ModalBottomSheet 显示 paneContent
            // 主内容区域直接放置 body
            body(this)

            // 仅当状态可见时才组合 ModalBottomSheet
            if (sidePanelVisible || bottomSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        state.close() // 用户通过点击 scrim 或滑动关闭时触发
                    },
                    sheetState = bottomSheetState,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.onSurface),
                ) {
                    Column(modifier = Modifier.navigationBarsPadding().padding(bottom = 16.dp)) {
                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                            Box {
                                with(contentScope) {
                                    paneContent(false) // Pass asSidePane = false
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Expanded: 使用 Row 布局显示 body 和 side pane
            Row(modifier = Modifier.fillMaxSize()) {
                // Body (主内容) 区域
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    content = { body(this) }
                )

                // Side Pane 区域
                AnimatedVisibility(
                    visible = sidePanelVisible,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it })
                ) {
                    androidx.compose.material3.Surface(
                        modifier = Modifier
                            .width(drawerWidth)
                            .fillMaxHeight()
                            .zIndex(1f),
                        color = MaterialTheme.colorScheme.surface,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.onSurface)
                    ) {
                        Box {
                            with(contentScope) {
                                paneContent(true)
                            }
                        }
                    }
                }
            }
        }
    }
}