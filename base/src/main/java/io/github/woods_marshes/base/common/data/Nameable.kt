package io.github.woods_marshes.base.common.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.woods_marshes.base.common.property.LocallyNamed

interface Nameable : LocallyNamed {
    val name: String
}

val Nameable.localizedName: String
    @Composable get() = nameStringRes?.let { stringResource(it) } ?: name