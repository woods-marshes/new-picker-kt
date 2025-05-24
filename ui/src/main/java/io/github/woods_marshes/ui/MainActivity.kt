package io.github.woods_marshes.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import io.github.woods_marshes.base.CollectionViewModel
import io.github.woods_marshes.base.ContentViewModel
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.base.common.data.collectAsResultState
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.ui.common.PickerKtActivityResult
import io.github.woods_marshes.ui.common.PickerKtActivityResult.Companion.RESULT_CONTRACT_KEY_PICKER_CONFIG
import io.github.woods_marshes.ui.common.compose.LocalPickerConfig
import io.github.woods_marshes.ui.common.data.extension.navHostRouteForPreviewByReferrer
import io.github.woods_marshes.ui.picker.library.LibraryViewModel
import io.github.woods_marshes.ui.selection.rememberContentSelectionController
import io.github.woods_marshes.ui.ui.theme.NewPickerKtTheme
import io.github.woods_marshes.ui.viewer.ContentPreviewScreenBody
import io.github.woods_marshes.ui.viewer.ContentViewerViewModel
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val contentPreviewViewModel: ContentViewerViewModel by viewModels()

    private lateinit var collectionViewModel: CollectionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config: PickerKtConfiguration = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(RESULT_CONTRACT_KEY_PICKER_CONFIG, PickerKtConfiguration::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(RESULT_CONTRACT_KEY_PICKER_CONFIG)!!
        }
        enableEdgeToEdge()

        setContent {
            collectionViewModel = hiltViewModel<CollectionViewModel, CollectionViewModel.Factory>(
                viewModelStoreOwner = this,
                key = "collectionViewModel"
            ) { factory ->
                factory.create(config)
            }

            NewPickerKtTheme {
                CompositionLocalProvider(LocalPickerConfig provides config) {
                    val selectionController = rememberContentSelectionController(
                        maxSelection = config.selection.maxSelection ?: Int.MAX_VALUE
                    )

                    val navController = rememberNavController()
                    val collections by collectionViewModel.collectionListFlow.collectAsResultState()
                    val context = LocalContext.current

                    NavHost(
                        navController = navController,
                        startDestination = "picker"
                    ) {
                        composable("picker") {
                            CompositionLocalProvider(LocalPickerConfig provides config) {
                                PickerScreen(
                                    controller = selectionController,
                                    contentGroups = LocalPickerConfig.current.mimeTypes
                                        .map { it.group }
                                        .distinct(),
                                    collections = collections,
                                    onContentClick = { content, referrerCollectionId ->
                                        contentPreviewViewModel.setPreviewContentId(content.id)
                                        navController.navigate(
                                            content.navHostRouteForPreviewByReferrer(
                                                referrerCollectionId
                                            )
                                        )
                                    },
                                    onSelectionConfirm = { finishActivityWithPickerResult(it) },
                                    onPickerCancel = { cancelAndClosePicker() }
                                )
                            }
                        }

                        composable(
                            route = "content/preview/{contentId}?referrer={referrer}",
                            arguments = listOf(
                                navArgument("contentId") { type = NavType.LongType },
                                navArgument("referrer") { type = NavType.StringType }
                            )
                        ) {

                            val referrer = it.arguments!!.getString("referrer")!!
                            val contentId =
                                contentPreviewViewModel.previewContentId.collectAsState().value

                            contentId ?: return@composable

                            val collectionLazyPagingItems: LazyPagingItems<Content> = when {
                                "collection" in referrer -> {
                                    val contentViewModel: ContentViewModel = viewModel(
                                        viewModelStoreOwner = context as ComponentActivity,
                                        key = referrer,
                                    )
                                    contentViewModel.contentListFlow.collectAsLazyPagingItems()
                                }

                                "library" in referrer -> {
                                    val libraryViewModel: LibraryViewModel = viewModel(
                                        viewModelStoreOwner = context as ComponentActivity,
                                        key = referrer,
                                    )
                                    libraryViewModel.recentContentList.collectAsLazyPagingItems()
                                }

                                else -> {
                                    throw IllegalStateException()
                                }
                            }

                            CompositionLocalProvider(LocalPickerConfig provides config) {
//                                if (collectionLazyPagingItems.itemCount > 0) {
                                if (false) {
                                    ContentPreviewScreenBody(
                                        onCurrentContentSelectionClick = {
                                            selectionController.toggleSelection(
                                                it
                                            )
                                        },
                                        selectionController = selectionController,
                                        contentId = contentId,
                                        collectionLazyPagingItems = collectionLazyPagingItems,
                                        onMainPreviewChange = {
                                            contentPreviewViewModel.setPreviewContentId(it.id)
                                        },
                                        onBackPress = {
                                            navController.navigateUp()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun finishActivityWithPickerResult(
        selection: List<Content>,
        resultCode: Int = RESULT_OK
    ) {
        val resultIntent = Intent()
        if (resultCode == RESULT_OK) {
            resultIntent.putExtra(
                PickerKtActivityResult.RESULT_CONTRACT_KEY_RESULT_URL_LIST_CONFIG,
                selection.map { it.uri }.toTypedArray()
            )
        }
        setResult(resultCode, resultIntent)
        finish()
    }

    private fun cancelAndClosePicker() {
        finishActivityWithPickerResult(emptyList(), RESULT_CANCELED)
    }
}