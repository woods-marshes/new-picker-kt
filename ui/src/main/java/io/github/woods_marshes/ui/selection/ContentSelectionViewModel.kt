package io.github.woods_marshes.ui.selection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.woods_marshes.base.content.model.Content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ContentSelectionViewModel @Inject constructor() : ViewModel() {
    private val _selection = MutableStateFlow<List<Content>>(listOf())
    val selection = _selection.asStateFlow()

    fun replaceSelection(newList: List<Content>) {
        _selection.tryEmit(newList)
    }
}