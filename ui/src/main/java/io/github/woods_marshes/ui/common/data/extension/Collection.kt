package io.github.woods_marshes.ui.common.data.extension

import io.github.woods_marshes.base.collection.model.CollectionBase

val CollectionBase.navHostRoute: String
    get() = "collections/$id"