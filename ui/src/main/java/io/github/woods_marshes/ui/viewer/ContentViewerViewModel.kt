package io.github.woods_marshes.ui.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.woods_marshes.base.content.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class ContentViewerViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
) : ViewModel() {
    private val _previewContentId = MutableStateFlow<Long?>(null)
    val previewContentId = _previewContentId.asStateFlow()

    val previewContent = _previewContentId.flatMapLatest {
        if (it != null) {
            contentRepository.ContentSingleSource(
                coroutineScope = viewModelScope,
                contentId = it
            ).flow
        } else {
            flowOf(null)
        }
    }

    fun setPreviewContentId(contentId: Long?) {
        _previewContentId.tryEmit(contentId)
    }
}