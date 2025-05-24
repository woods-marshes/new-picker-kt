package io.github.woods_marshes.ui.picker.nav.data

import io.github.woods_marshes.base.collection.model.CollectionBase
import io.github.woods_marshes.ui.common.data.extension.navHostRoute
import io.github.woods_marshes.ui.picker.nav.data.preset.PresetNavLocation

sealed class NavLocation(
    val navHostRoute: String,
    val id: String
) {
    class Preset(presetNavLocation: PresetNavLocation) : NavLocation(
        id = if (presetNavLocation.correspondingCollection != null) {
            locationIdOfCollection(presetNavLocation.correspondingCollection!!.id)
        } else {
            locationIdOfLibrary(presetNavLocation.name)
        },
        navHostRoute = presetNavLocation.navHostRoute,
    )

    class Constant(route: String) : NavLocation(
        id = route,
        navHostRoute = route,
    )

    class Collection(collection: CollectionBase) : NavLocation(
        id = locationIdOfCollection(collection),
        navHostRoute = collection.navHostRoute,
    )

    companion object {
        fun locationIdOfCollection(id: String) = "collection-$id"
        fun locationIdOfCollection(collection: CollectionBase) = "collection-${collection.id}"
        fun locationIdOfLibrary(id: String) = "library-$id"
    }
}