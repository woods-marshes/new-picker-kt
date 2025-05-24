package io.github.woods_marshes.ui.common.data.extension

import io.github.woods_marshes.base.content.model.Content

fun Content.navHostRouteForPreviewByReferrer(referrer: String): String {
    return "content/preview/$id?referrer=${referrer}"
}