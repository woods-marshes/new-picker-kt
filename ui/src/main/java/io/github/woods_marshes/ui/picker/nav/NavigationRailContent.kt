package io.github.woods_marshes.ui.picker.nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.woods_marshes.base.common.data.localizedName
import io.github.woods_marshes.ui.component.collection.CountBadge
import io.github.woods_marshes.ui.component.nav.DrawerDividerItem
import io.github.woods_marshes.ui.picker.nav.data.NavLocation
import io.github.woods_marshes.ui.picker.nav.data.preset.PresetNavLocation

@Composable
fun NavigationRailContent(
    modifier: Modifier = Modifier,
    selectionInCollectionMap: Map<String, Int>,
    presetNavLocations: List<PresetNavLocation>,
    navRoute: String?,
    onNavIconClick: () -> Unit,
    onClick: (NavLocation) -> Unit
) {
    NavigationRail(
        modifier = modifier.fillMaxHeight(),
        header = {
            IconButton(onClick = { onNavIconClick() }) {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            presetNavLocations
                .groupBy { it.group }
                .entries
                .forEachIndexed { i, (_, u) ->
                    if (i != 0) {
                        DrawerDividerItem()
                    }
                    u.forEach {
                        NavigationRailItem(
                            selected = it.navHostRoute == navRoute,
                            onClick = { onClick(NavLocation.Preset(it)) },
                            label = { Text(it.localizedName, maxLines = 1) },
                            icon = {
                                if (it == PresetNavLocation.AllFolders) {
                                    BadgedBox(
                                        badge = {
                                            val selectionCount =
                                                selectionInCollectionMap.values.fold(0) { x, y -> x + y }
                                            CountBadge(
                                                count = selectionCount,
                                                contentDescription = null,
                                                icon = null
                                            )
                                        }
                                    ) {
                                        Icon(
                                            it.icon,
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    Icon(
                                        it.icon,
                                        contentDescription = null
                                    )
                                }
                            },
                            alwaysShowLabel = false
                        )
                    }
                }
        }
    }
}