package io.github.woods_marshes.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.woods_marshes.base.builder.PickerKt
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.base.collection.CollectionRepository
import io.github.woods_marshes.base.common.data.transform
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.base.contentresolver.PredefinedCollectionIdSet
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

val default = PickerKt.picker {
    allowMimes {
        add { MimeType.Jpeg }
        add { MimeType.Png }
        add { MimeType.Gif }
        add { MimeType.Svg }
        add { MimeType.Mpeg4 }
        add { MimeType.MsWordDoc2007 }
        add { MimeType.Mp3 }
        add { MimeType.OggAudio }
    }
}

@HiltViewModel(assistedFactory = CollectionViewModel.Factory::class)
class CollectionViewModel @AssistedInject constructor(
    private val collectionRepository: CollectionRepository,
    @Assisted config: PickerKtConfiguration
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(config: PickerKtConfiguration = default): CollectionViewModel
    }

    val collectionListFlow = collectionRepository.collectionListFlow(
        coroutineScope = viewModelScope,
        config = config
    )
        .map {
        it.transform { data ->
            data.filterNot { x ->
                x.id in PredefinedCollectionIdSet
            }
        }
    }
        .onEach {
//            Timber.d("collectionListFlow: ${it.data?.size}")
        }


}