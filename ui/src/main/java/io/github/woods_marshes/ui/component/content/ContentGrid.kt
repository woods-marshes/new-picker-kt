package io.github.woods_marshes.ui.component.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import io.github.woods_marshes.base.LazyListItem
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.ui.common.compose.itemsIndexed
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale.forLanguageTag

@Composable
fun MainContentPickerGrid(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    scrollConnection: NestedScrollConnection? = null,
    mimeFilterSet: Set<MimeType>,
    content: LazyPagingItems<LazyListItem<out Content>>,
    selectionVisible: Boolean = false,
    selectionMap: Map<Long, Int>,
    onContentItemClick: (Content) -> Unit,
    onContentCheckClick: (Content) -> Unit
) {
    LazyVerticalGrid(
        state = state,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .let { if (scrollConnection != null) it.nestedScroll(scrollConnection) else it },
        columns = GridCells.Adaptive(120.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        contentItems(
            content = content,
            mimeFilterSet = mimeFilterSet,
            selectionVisible = selectionVisible,
            selectionMap = selectionMap,
            onContentItemClick = onContentItemClick,
            onContentCheckClick = onContentCheckClick
        )
    }
}

fun LazyGridScope.contentItems(
    content: LazyPagingItems<LazyListItem<out Content>>,
    mimeFilterSet: Set<MimeType>,
    selectionVisible: Boolean = false,
    selectionMap: Map<Long, Int>,
    onContentItemClick: (Content) -> Unit,
    onContentCheckClick: (Content) -> Unit
) {
    itemsIndexed(
        items = content,
        span = {
            when (it) {
                is LazyListItem.Data -> GridItemSpan(1)
                is LazyListItem.TimeGroupHeader -> GridItemSpan(1)
            }
        }
    ) { _, item ->

        if (item == null) {
            Box(modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth())
            return@itemsIndexed
        }

        when(item) {
            is LazyListItem.Data -> {
                val c = item.data
                CoilContentGridItem(
                    modifier = Modifier.animateItem(),
                    enabled = selectionVisible && (if (mimeFilterSet.isNotEmpty()) c.mimeType in mimeFilterSet else true),
                    content = c,
                    editModeActivated = true,
                    selectionIndex = selectionMap[c.id] ?: -1,
                    onClick = { onContentItemClick(c) },
                    onCheckClick = { onContentCheckClick(c) }
                )
            }
            is LazyListItem.TimeGroupHeader -> {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    val timeZone = TimeZone.currentSystemDefault()
                    val date = item.time.toLocalDateTime(timeZone)
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = date.month.getDisplayName(TextStyle.FULL,
                            forLanguageTag(Locale.current.toLanguageTag())),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = date.year.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}