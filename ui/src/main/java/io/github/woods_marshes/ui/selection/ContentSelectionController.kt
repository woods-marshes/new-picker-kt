package io.github.woods_marshes.ui.selection

import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.woods_marshes.base.common.property.groupById
import io.github.woods_marshes.base.common.serializer.InstantSerializer
import io.github.woods_marshes.base.common.unit.Byte
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.content.model.timeSortedValues
import io.github.woods_marshes.base.contentresolver.MimeType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun rememberContentSelectionController(
    initialSelections: List<Content> = emptyList(),
    maxSelection: Int = 100,
    onSelectionChanged: ((List<Content>) -> Unit)? = null
): ContentSelectionController {
    val controller = rememberSaveable(initialSelections, saver = ContentSelectionController.Saver) {
        ContentSelectionController(initialSelections, maxSelection = maxSelection)
    }

    LaunchedEffect(controller.selection.keys.joinToString(separator = ",")) {

        val timeSortedValues = controller.selection.timeSortedValues().take(maxSelection)
        controller.canonicalSelectionList.clear()
        controller.canonicalSelectionList.addAll(timeSortedValues)

        controller.canonicalSelectionOrderMap.clear()
        controller.canonicalSelectionOrderMap.putAll(timeSortedValues.mapIndexed { i, it -> it.id to i })

        onSelectionChanged?.invoke(controller.canonicalSelectionList)
    }

    return controller
}

class ContentSelectionController internal constructor(
    initialSelections: List<Content>,
    val maxSelection: Int = Int.MAX_VALUE,
) :
    SelectionController<Content, Long> {

    // TODO: We could use StateObject or something
    internal val selection = mutableStateMapOf(
        *initialSelections
            .take(maxSelection)
            .map { it.id to (Clock.System.now() to it) }
            .toTypedArray()
    )

    private fun addNewSelection(item: Content, timestamp: Instant = Clock.System.now()): Boolean {
        val beforeCount = selection.size

        if (beforeCount >= maxSelection) {
            return false
        }

        selection.putIfAbsent(
            item.id,
            (selection[item.id]?.first ?: timestamp) to item
        )

        return selection.size > beforeCount
    }

    private fun removeNewSelection(item: Content): Boolean {
        val beforeCount = selection.size

        if (item in this) {
            selection.remove(item.id)
        }

        return selection.size < beforeCount
    }

    override val size: Int
        get() = selection.size

    override operator fun contains(itemId: Long) = itemId in selection

    override operator fun contains(item: Content) = item.id in selection

    override fun select(item: Content): Boolean {
        return addNewSelection(item)
    }

    override fun select(item: List<Content>) {
        var timestamp = Clock.System.now().toEpochMilliseconds()
        item.forEach {
            addNewSelection(it, timestamp = Instant.fromEpochMilliseconds(timestamp))
            timestamp++
        }
    }

    override fun unselect(item: Content): Boolean {
        return removeNewSelection(item)
    }

    override fun toggleSelection(item: Content) {
        if (item.id !in this) select(item) else unselect(item)
    }

    override fun clear() = selection.clear()

    override fun invert(items: Collection<Content>) {
        val selectedItem = selection.toMap()

        selection.clear()
        items.forEach {
            if (it.id !in selectedItem) {
                selection.putIfAbsent(it.id, Clock.System.now() to it)
            }
        }
    }

    override fun toString(): String {
        return "CollectionSelectionController(selection=$selection)"
    }

    override val canonicalSelectionList = mutableStateListOf<Content>()

    override val canonicalSelectionOrderMap = mutableStateMapOf<Long, Int>()

    override fun replaceAllWith(newSelection: List<Content>) {
        val selectionSet = selection.map { x -> x.key }.toHashSet()
        val tempSelectionMap = newSelection.groupById()
        val tempSelectionSet = tempSelectionMap.keys

        val idToBeRemoved = selectionSet - tempSelectionSet
        val idToBeAdded = tempSelectionSet - selectionSet

        idToBeRemoved.forEach { id -> selection.remove(id) }
        idToBeAdded.forEach { id ->
            selection.putIfAbsent(
                id,
                Clock.System.now() to tempSelectionMap[id]!!.first()
            )
        }
    }

    override fun removeIf(predicate: (Content) -> Boolean) {
        selection.forEach { (t, u) ->
            if (predicate(u.second)) {
                selection.remove(t)
            }
        }
    }

    @Keep
    @Serializable
    private class ContentSelectionSaverEntry(
        @Serializable(with = InstantSerializer::class) val timeSelected: Instant,
        val content: Content
    ) {
        constructor(pair: Pair<Instant, Content>) : this(pair.first, pair.second)

        fun toPair(): Pair<Instant, Content> = timeSelected to content
    }

    companion object {
        /**
         * The default [Saver] implementation for [ContentSelectionController].
         */
        val Saver = mapSaver(
            save = {
                it.selection
                    .mapKeys { it.key.toString() }
                    .mapValues { Json.encodeToString(ContentSelectionSaverEntry(it.value)) }
            },
            restore = {
                ContentSelectionController(
                    initialSelections = it.values.toList()
                        .map {
                            Json.decodeFromString<ContentSelectionSaverEntry>(it as String).toPair()
                        }
                        .timeSortedValues()
                )
            }
        )
    }
}


@Composable
@Preview
private fun Test() {
    val contents = remember {
        (0 until 10)
            .map {
                Content(
                    id = it.toLong(),
                    name = "Hello word $it",
                    dateAdded = Clock.System.now(),
                    mimeType = MimeType.Jpeg,
                    size = Byte(100L),
                    collectionId = "Collection $it",
                )
            }
    }
    val context = LocalContext.current
    val controller = rememberContentSelectionController(
        maxSelection = 10
    ) {
        Toast.makeText(context, "Selection: ${it.size} items", Toast.LENGTH_SHORT).show()
    }

    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { controller.invert(contents) },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("INVERT SELECTION")
        }

        contents.forEach {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(it.id.toString() + " (${it.id in controller})")
                Button(onClick = {
                    if (it !in controller) {
                        controller.select(it)
                    } else {
                        controller.unselect(it)
                    }
                }) {
                    Text(text = "SELECT")
                }
            }
        }
    }
}