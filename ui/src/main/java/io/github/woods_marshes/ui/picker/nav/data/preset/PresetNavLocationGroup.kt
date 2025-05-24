package io.github.woods_marshes.ui.picker.nav.data.preset

import androidx.annotation.StringRes
import io.github.woods_marshes.base.common.data.Nameable
import io.github.woods_marshes.ui.R

enum class PresetNavLocationGroup(@StringRes override val nameStringRes: Int) : Nameable {
    Common(nameStringRes = R.string.ui_nav_group_general),
    Libraries(nameStringRes = R.string.ui_nav_group_library),
}