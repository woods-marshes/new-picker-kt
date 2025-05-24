package io.github.woods_marshes.ui.component.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.floor

@DslMarker
annotation class VerticalGridScopeMarker

@VerticalGridScopeMarker
interface VerticalGridScope {

    fun item(content: @Composable VerticalGridItemScope.() -> Unit)

    fun <T> items(items: List<T>, content: @Composable VerticalGridItemScope.(T) -> Unit)
}

class VerticalGridScopeImpl : VerticalGridScope {

    internal val itemList = mutableListOf<@Composable VerticalGridItemScope.() -> Unit>()

    override fun item(content: @Composable VerticalGridItemScope.() -> Unit) {
        itemList.add(content)
    }

    override fun <T> items(items: List<T>, content: @Composable VerticalGridItemScope.(T) -> Unit) {
        items.forEach {
            itemList.add { content(it) }
        }
    }
}


interface VerticalGridItemScope {

    fun Modifier.fillParentMaxWidth(fraction: Float = 1f): Modifier

}

class VerticalGridItemScopeImpl(val width: Dp) : VerticalGridItemScope {
    override fun Modifier.fillParentMaxWidth(fraction: Float): Modifier {
        return width(width * fraction)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
@OptIn(ExperimentalLayoutApi::class)
fun VerticalGrid(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemVerticalAlignment: Alignment.Vertical = Alignment.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    maxLines: Int = Int.MAX_VALUE,
    minWidth: Dp,
    content: VerticalGridScope.() -> Unit
) {

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current

        val calculatedItemWidth = remember(maxWidth, density, minWidth, horizontalArrangement) {
            with(density) {

                val availableWidthPx = maxWidth.toPx()
                val minWidthPx = minWidth.toPx()

                val mainAxisSpacingPx = horizontalArrangement.spacing.roundToPx()

                if (minWidthPx <= 0f) {
                    return@remember maxWidth
                }

                val estimatedNColumns = floor(availableWidthPx / minWidthPx)
                val nColumns = estimatedNColumns.coerceAtLeast(1f)


                val totalMinWidthNeededPx = minWidthPx * nColumns

                val totalSpacingPx = if (nColumns > 1) mainAxisSpacingPx * (nColumns - 1) else 0f

                val extraWidthPx = availableWidthPx - totalMinWidthNeededPx - totalSpacingPx

                val extraWidthForEachItemPx = if (extraWidthPx > 0) extraWidthPx / nColumns else 0f

                (minWidthPx + extraWidthForEachItemPx).toDp()
            }
        }

        val scope = remember(content) { VerticalGridScopeImpl().apply(content) }
        val itemScope = remember(calculatedItemWidth) { VerticalGridItemScopeImpl(calculatedItemWidth) }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            itemVerticalAlignment = itemVerticalAlignment,
            maxItemsInEachRow = maxItemsInEachRow,
            maxLines = maxLines,
            content = {
                scope.itemList.forEach { itemComposable ->
                    Box(modifier = Modifier.width(calculatedItemWidth)) {
                        itemComposable(itemScope)
                    }
                }
            }
        )
    }
}