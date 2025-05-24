package io.github.woods_marshes.base.collection.model

import android.text.format.DateUtils
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.github.woods_marshes.base.R
import io.github.woods_marshes.base.common.unit.Byte
import io.github.woods_marshes.base.content.model.Content
import io.github.woods_marshes.base.contentresolver.MimeType
import kotlinx.datetime.Instant

data class Collection(
    @StringRes override val nameStringRes: Int? = null,
    override val id: String,
    override val name: String,
    val contentCount: Int,
    val timeAdded: Instant,
    val size: Byte,
    val lastContentItem: Content?,
    val contentGroupCounts: Map<MimeType, Int>?,
) : CollectionBase {
    val relativeTimeString: String
        @Composable get() {
            return DateUtils.getRelativeTimeSpanString(LocalContext.current, timeAdded.toEpochMilliseconds(), true).toString()
        }
}

val Collection.finalName: String
    @Composable get() {
        return nameStringRes?.let { stringResource(it) }
            ?: name.takeIf { it.isNotEmpty() }
            ?: stringResource(R.string.base_common_unknown)
    }
