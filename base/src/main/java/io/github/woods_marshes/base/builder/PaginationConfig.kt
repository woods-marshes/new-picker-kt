package io.github.woods_marshes.base.builder

import kotlinx.serialization.Serializable

@ConsistentCopyVisibility
@Serializable
data class PaginationConfig internal constructor(
    val pageSize: Int,
    val prefetchDistance: Int
) {
    init {
        assert(pageSize > 0) {
            "pageSize must be greater than 0"
        }
    }

    @PickerKtBuilderDslMarker
    class Builder internal constructor(
        private var pageSize: Int = 10,
        private var prefetchDistance: Int = 30
    ) {
        fun pageSize(block: () -> Int) {
            pageSize = block()
        }

        fun prefetchDistance(block: () -> Int) {
            prefetchDistance = block()
        }

        fun build(): PaginationConfig {
            return PaginationConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance
            )
        }
    }
}
