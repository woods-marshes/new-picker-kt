package io.github.woods_marshes.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.woods_marshes.base.ContentViewModel
import io.github.woods_marshes.base.collection.model.Collection
import io.github.woods_marshes.base.common.data.Result
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.content.model.groupByCollectionAndCount
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.base.contentresolver.PredefinedCollections
import io.github.woods_marshes.ui.common.compose.LocalPickerConfig
import io.github.woods_marshes.ui.component.collection.CountBadge
import io.github.woods_marshes.ui.layout.PickerAppDrawerState
import io.github.woods_marshes.ui.layout.PickerAppLayout
import io.github.woods_marshes.ui.layout.rememberPickerAppDrawerState
import io.github.woods_marshes.ui.picker.collection.CollectionScreen
import io.github.woods_marshes.ui.picker.library.LibraryScreen
import io.github.woods_marshes.ui.picker.library.LibraryViewModel
import io.github.woods_marshes.ui.picker.nav.DrawerContent
import io.github.woods_marshes.ui.picker.nav.NavigationRailContent
import io.github.woods_marshes.ui.picker.nav.data.NavLocation
import io.github.woods_marshes.ui.picker.nav.data.preset.PresetNavLocation
import io.github.woods_marshes.ui.picker.selectionmanager.ContentSelectionModal
import io.github.woods_marshes.ui.selection.ContentSelectionController
import kotlinx.coroutines.launch

@Composable
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalStdlibApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class
)
fun PickerScreen(
    modifier: Modifier = Modifier,
    contentGroups: List<MimeType.Group>,
    collections: Result<List<Collection>>,
    controller: ContentSelectionController,
    onContentClick: (Content, referrer: String) -> Unit,
    onSelectionConfirm: (List<Content>) -> Unit,
    onPickerCancel: () -> Unit
) {
    val navController = rememberNavController()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    // Initialize drawerState based on WindowSizeClass
    val drawerState = rememberPickerAppDrawerState(
        initialValue = if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) DrawerValue.Open else DrawerValue.Closed
    )
    val coroutineScope = rememberCoroutineScope()

    fun toggleDrawer() = coroutineScope.launch {
        if (drawerState.isClosed) drawerState.open() else drawerState.close()
    }

    fun NavController.navigateWithSaveState(route: String) {
        navigate(route = route) {
            navController.graph.startDestinationRoute?.let { route ->
                popUpTo(route) {
                    saveState = false
                }
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BackHandler {
        if (navController.currentBackStackEntry?.destination?.route == PresetNavLocation.AllFolders.navHostRoute) {
            navController.navigateWithSaveState("cancel")
        }
    }


    val navRouteEntry by navController.currentBackStackEntryFlow.collectAsState(initial = null)
    val navRoute = navRouteEntry?.destination?.route

    val selectionInCollectionMap by rememberUpdatedState(controller.canonicalSelectionList.groupByCollectionAndCount())

    val presetNavLocations by rememberUpdatedState(
        newValue = PresetNavLocation.entries
            .filter {
                (if (contentGroups.isEmpty()) true else (it.mimeTypeGroup in contentGroups || it.mimeTypeGroup == null))
            }
    )

    PickerAppLayout(
        modifier = modifier.statusBarsPadding(),
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                drawerState = drawerState,
                collections = collections,
                onClick = {
                    navController.navigateWithSaveState(it.navHostRoute)
                    if (PickerAppDrawerState.shouldUseModalDrawer(windowSizeClass)) toggleDrawer()
                },
                currentRoute = navRouteEntry?.arguments?.getString("collectionId") ?: navRoute ?: "",
                selectionInCollectionMap = selectionInCollectionMap,
                presetNavLocations = presetNavLocations
            )
        },
        floatingActionButton = {
            Column(
                modifier = Modifier.navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigateWithSaveState("cancel")
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    content = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                )

                FloatingActionButton(onClick = {
                    navController.navigateWithSaveState("selection")
                }) {
                    BadgedBox(
                        badge = {
                            CountBadge(
                                count = controller.size,
                                icon = null,
                                contentDescription = "Totally, you selected ${controller.size} items."
                            )
                        }
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        },
        sidebar = {
            NavigationRailContent(
                modifier = Modifier.align(Alignment.Center),
                onNavIconClick = { toggleDrawer() },
                navRoute = navRoute,
                onClick = {
                    navController.navigateWithSaveState(it.navHostRoute)
                },
                selectionInCollectionMap = selectionInCollectionMap,
                presetNavLocations = presetNavLocations
            )
        }
    ) {
        val context = LocalContext.current
        val config = LocalPickerConfig.current
        NavHost(
            navController = navController,
            startDestination = PresetNavLocation.AllFolders.navHostRoute
        ) {
            presetNavLocations
                .filter { !it.hasCorrespondingCollection }
                .map { NavLocation.Preset(it) to it }
                .forEach { (navLocation, enum) ->
                    composable(route = navLocation.navHostRoute) {
                        LibraryScreen(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = hiltViewModel<LibraryViewModel, LibraryViewModel.Factory>(
                                viewModelStoreOwner = context as ComponentActivity,
                                key = navLocation.id
                            ) { factory ->
                                factory.create(
                                    mimeTypeGroup = enum.mimeTypeGroup,
                                    config = config
                                )
                            },
                            selectionMap = controller.canonicalSelectionOrderMap,
                            navLocation = enum,
                            onNavIconClick = { toggleDrawer() },
                            onContentItemClick = { onContentClick(it, navLocation.id) },
                            onContentCheckClick = { controller.toggleSelection(it) },
                            onCollectionClick = {
                                navController.navigateWithSaveState("collections/${it.id}?showNavUpIcon=true&mimeGroup=${enum.mimeTypeGroup?.name}")
                            }
                        )
                    }
                }

            // Since we do have some Collections as pre-defined ones, we cannot have a destination w/ arguments
            // as the starting destination, so we have to separately define them here.
            PredefinedCollections
                .map { NavLocation.Collection(it) to it }
                .forEach { (navLocation, enum) ->
                    composable(route = navLocation.navHostRoute) {
                        CollectionScreen(
                            onNavIconClick = { toggleDrawer() },
                            viewModel = hiltViewModel<ContentViewModel, ContentViewModel.Factory>(
                                viewModelStoreOwner = context as ComponentActivity,
                                key = navLocation.id
                            ) { factory ->
                                factory.create(
                                    collectionId = enum.id,
                                    config = config
                                )
                            },
                            showNavUpIcon = false,
                            selectionController = controller,
                            onItemClick = { onContentClick(it, navLocation.id) }
                        )
                    }
                }

            composable(
                route = "collections/{collectionId}?showNavUpIcon={showNavUpIcon}&mimeGroup={mimeGroup}",
                arguments = listOf(
                    navArgument("collectionId") { type = NavType.StringType },
                    navArgument("mimeGroup") { type = NavType.StringType; nullable = true },
                    navArgument("showNavUpIcon") { type = NavType.BoolType; defaultValue = false }
                )
            ) {
                val collectionId = it.arguments!!.getString("collectionId")!!
                val mimeGroup =
                    it.arguments!!.getString("mimeGroup")?.let { MimeType.Group.valueOf(it) }
                val showNavUpIcon = it.arguments!!.getBoolean("showNavUpIcon", false)
                val navLocationId = NavLocation.locationIdOfCollection(collectionId)

                CollectionScreen(
                    showNavUpIcon = showNavUpIcon,
                    onNavIconClick = {
                        if (showNavUpIcon) navController.navigateUp() else toggleDrawer()
                    },
                    viewModel = hiltViewModel<ContentViewModel, ContentViewModel.Factory>(
                        viewModelStoreOwner = context as ComponentActivity,
                        key = navLocationId
                    ) {factory ->
                        factory.create(
                            collectionId = collectionId,
                            config = config
                        )
                    },
                    selectionController = controller,
                    onItemClick = { onContentClick(it, navLocationId) }
                )
            }


            dialog(
                route = "selection",
                dialogProperties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
                ContentSelectionModal(
                    selectedContents = controller.canonicalSelectionList,
                    onCloseClick = {
                        controller.replaceAllWith(it)
                        navController.navigateUp()
                    },
                    onConfirmed = {
                        // TODO: Show a confirmation dialog and Save Result
                        controller.replaceAllWith(it)
                        navController.navigateUp()
                        navController.navigateWithSaveState("confirm")
                    }
                )
            }

            dialog(route = "confirm") {
                val limit = remember { 19 }
                val selection = controller.canonicalSelectionList

                AlertDialog(
                    onDismissRequest = { navController.navigateUp() },
                    icon = {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    title = {
                        Text(
                            "Confirm your selections?",
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                buildAnnotatedString {
                                    append("Confirm to select the following ")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("${selection.size} items")
                                    }
                                },
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(selection.size.coerceIn(1, 5)),
                                modifier = Modifier
                                    .sizeIn(maxHeight = 300.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentPadding = PaddingValues(4.dp),
                            ) {
                                items(
                                    items = selection.take(limit),
                                    key = { it.id }
                                ) {
                                    val painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current).data(it.uri).crossfade(true).build()
                                    )
                                    Image(
                                        painter = painter,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.Center
                                    )
                                }

                                if (selection.size > limit) {
                                    item {
                                        Surface(
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp)),
                                            tonalElevation = LocalAbsoluteTonalElevation.current + 1.dp
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("+${selection.size - limit} more")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onSelectionConfirm(selection)
                                // navController.navigateUp()
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { navController.navigateUp() }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            dialog(route = "cancel") {
                AlertDialog(
                    onDismissRequest = { navController.navigateUp() },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    title = {
                        Text(
                            "Cancel and Exit the selection?",
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Text(
                            "The selection is discarded if you exit.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Cancel")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { onPickerCancel() }
                        ) {
                            Text("Exit Anyway")
                        }
                    }
                )
            }
        }
    }
}