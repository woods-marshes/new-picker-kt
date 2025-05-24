package io.github.woods_marshes.ui.layout

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.woods_marshes.ui.layout.PickerAppDrawerState.Companion.shouldUseModalDrawer

class AppLayout(boxScope: BoxScope, val windowSizeClass: WindowSizeClass) : BoxScope by boxScope

@OptIn(ExperimentalMaterial3Api::class)
class PickerAppDrawerState internal constructor(
    val materialDrawerState: DrawerState,
    val initialValue: DrawerValue = DrawerValue.Closed,
    internal var windowSizeClass: WindowSizeClass
) {

    var drawerValue by mutableStateOf(initialValue)
        private set

    suspend fun open() {
        drawerValue = DrawerValue.Open
        if (shouldUseModalDrawer(windowSizeClass)) {
            materialDrawerState.open()
        }
    }

    suspend fun close() {
        drawerValue = DrawerValue.Closed
        materialDrawerState.close()
    }

    val isClosed: Boolean
        get() = if (!shouldUseModalDrawer(windowSizeClass)) {
            drawerValue == DrawerValue.Closed
        } else {
            materialDrawerState.currentValue == DrawerValue.Closed
        }

    val isOpen: Boolean
        get() = if (!shouldUseModalDrawer(windowSizeClass)) {
            drawerValue == DrawerValue.Open
        } else {
            materialDrawerState.currentValue == DrawerValue.Open
        }

    companion object {
        fun shouldUseModalDrawer(windowSizeClass: WindowSizeClass): Boolean =
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        val allowModalDrawer: Boolean
            @Composable get() {
                val currentWindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
                return shouldUseModalDrawer(currentWindowSizeClass)
            }

        val Saver = Saver<PickerAppDrawerState, Triple<DrawerValue, DrawerValue, WindowSizeClass>>(
            save = {
                Triple(it.materialDrawerState.currentValue, it.drawerValue, it.windowSizeClass)
            },
            restore = { (materialDrawerValue, drawerValue, windowSizeClass) ->
                PickerAppDrawerState(
                    materialDrawerState = DrawerState(materialDrawerValue),
                    initialValue = drawerValue,
                    windowSizeClass = windowSizeClass
                )
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun rememberPickerAppDrawerState(initialValue: DrawerValue): PickerAppDrawerState {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val materialDrawerState = rememberDrawerState(
        initialValue = if (shouldUseModalDrawer(windowSizeClass)) initialValue else DrawerValue.Closed
    )

    val pickerAppDrawerState = rememberSaveable(windowSizeClass, saver = PickerAppDrawerState.Saver) {
        PickerAppDrawerState(materialDrawerState = materialDrawerState, windowSizeClass = windowSizeClass)
    }

    LaunchedEffect(windowSizeClass) {
        pickerAppDrawerState.windowSizeClass = windowSizeClass

        with(pickerAppDrawerState) {
            val useModal = shouldUseModalDrawer(windowSizeClass)
            // 如果切换到了非模态，强制关闭 Material Drawer
            if (!useModal && materialDrawerState.isOpen) {
                materialDrawerState.close() // 关闭模态视图
            }
            if (useModal) {
                when(drawerValue) {
                    DrawerValue.Closed -> materialDrawerState.close()
                    DrawerValue.Open -> materialDrawerState.open()
                }
            } else {
                if (materialDrawerState.isOpen) {
                    materialDrawerState.close()
                }
            }
        }
    }

    LaunchedEffect(pickerAppDrawerState.drawerValue) {
        if (shouldUseModalDrawer(windowSizeClass)) {
            when (pickerAppDrawerState.drawerValue) {
                DrawerValue.Closed -> pickerAppDrawerState.close()
                DrawerValue.Open -> pickerAppDrawerState.open()
            }
        }
    }

    LaunchedEffect(pickerAppDrawerState.materialDrawerState.currentValue) {
        when (pickerAppDrawerState.materialDrawerState.currentValue) {
            DrawerValue.Closed -> pickerAppDrawerState.close()
            DrawerValue.Open -> pickerAppDrawerState.open()
        }
    }

    return pickerAppDrawerState
}


@Composable
fun PickerAppLayout(
    modifier: Modifier = Modifier,
    navColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    drawerState: PickerAppDrawerState,
    drawerContent: (@Composable ColumnScope.() -> Unit)? = null,
    sidebar: @Composable BoxScope.() -> Unit,
    floatingActionButton: @Composable () -> Unit,
    content: @Composable AppLayout.() -> Unit
) {
    val usePermanentDrawer = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val useModalDrawer = !usePermanentDrawer
    val materialDrawerState = drawerState.materialDrawerState
    val isExpandedWindow by rememberUpdatedState(newValue = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND))
    val windowRoundedCorner by animateDpAsState(
        targetValue = if (isExpandedWindow) 24.dp else 0.dp,
        label = "Rounded Corner"
    )
    val navigationDrawerContentComposable: @Composable () -> Unit = {
        if (useModalDrawer) {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight(),
                content = {
                    drawerContent?.invoke(this)
                }
            )
        } else {
            val currentDrawerState = drawerState
            if (currentDrawerState.isOpen) {
                PermanentDrawerSheet(
                    modifier = Modifier.width(360.dp).fillMaxHeight(),
                    content = {
                        drawerContent?.invoke(this)
                    }
                )
            } else {
                Box(modifier = Modifier.width(360.dp).fillMaxHeight()) {
                    sidebar.invoke(this)
                }
            }
        }
    }


    if (useModalDrawer) {
        ModalNavigationDrawer(
            modifier = modifier.fillMaxSize(),
            drawerState = materialDrawerState,
            drawerContent = navigationDrawerContentComposable,
            content = {
                Scaffold(
                    floatingActionButtonPosition = FabPosition.End,
                    floatingActionButton = { floatingActionButton() },
                    content = { paddingValues ->
                        Box(
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize()
                        ) {
                            AppLayout(this, windowSizeClass).content()
                        }
                    }
                )
            }
        )
    } else {
        Row(modifier = modifier.fillMaxSize().background(color = navColor)) {
            PermanentNavigationDrawer(
                drawerContent = navigationDrawerContentComposable,
                content = {
                    Scaffold(
                        floatingActionButtonPosition = FabPosition.End,
                        floatingActionButton = { floatingActionButton() },
                        content = { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .padding(top = if (isExpandedWindow && windowRoundedCorner > 0.dp) windowRoundedCorner else 0.dp) // Add padding if rounded corner is applied
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(topStart = windowRoundedCorner))
                            ) {
                                AppLayout(this, windowSizeClass).content()
                            }
                        }
                    )
                }
            )
        }
    }
}