package io.github.woods_marshes.ui.picker.folder

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.woods_marshes.base.collection.model.Collection
import io.github.woods_marshes.base.common.data.Result
import io.github.woods_marshes.base.common.data.data
import io.github.woods_marshes.ui.common.compose.animation.AnimatedAppBarIconVisibility
import io.github.woods_marshes.ui.component.collection.CollectionGridItem
import io.github.woods_marshes.ui.component.common.Placeholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionListScreen(
    modifier: Modifier = Modifier,
    collections: Result<List<Collection>>,
    state: LazyGridState = rememberLazyGridState(),
    onCollectionClick: (Collection) -> Unit,
    onNavIconClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Folders") },
                navigationIcon = {
                    AnimatedAppBarIconVisibility(visible = true) {
                        IconButton(onClick = onNavIconClick) {
                            Icon(Icons.Outlined.ArrowUpward, contentDescription = null)
                        }
                    }
                }
            )
        },
    ) {
        AnimatedContent(modifier = modifier.padding(it), targetState = collections) {
            when(it) {
                Result.Loading -> {
                    CircularProgressIndicator()
                }
                is Result.Success -> {
                    LazyVerticalGrid(
                        state = state,
                        columns = GridCells.Adaptive(220.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        itemsIndexed(items = collections.data ?: return@LazyVerticalGrid) { i, it ->
                            CollectionGridItem(
                                collection = it,
                                onClick = { onCollectionClick(it) }
                            )
                        }
                    }
                }
                is Result.Error -> {
                    Placeholder(title = { Text(text = "Fail to load Collections") }) {
                        Icon(
                            Icons.Outlined.Folder,
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
        }
    }
}