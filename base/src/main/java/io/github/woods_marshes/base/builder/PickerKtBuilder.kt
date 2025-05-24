package io.github.woods_marshes.base.builder

@DslMarker
annotation class PickerKtBuilderDslMarker

object PickerKt {

    /**
     * Example of usage:
     * ```
     * val config = PickerKt.picker {
     *   allowMimes {
     *       add { MimeType.Jpeg }
     *       add { MimeType.Gif }
     *   }
     *   selection {
     *       maxSelection { null }
     *   }
     *   orderBy {
     *       add { Ordering(column = ContentResolverColumn.ContentMimeType) }
     *   }
     *   predicate {
     *       ContentColumn(column = ContentResolverColumn.CollectionId) equal valueOf("")
     *       ContentColumn(column = ContentResolverColumn.ByteSize) greaterThan valueOf(0)
     *   }
     * }
     * ```
     */
    fun picker(builderScope: PickerKtConfiguration.Builder.() -> Unit): PickerKtConfiguration {
        return PickerKtConfiguration.Builder().apply(builderScope).build()
    }
}

