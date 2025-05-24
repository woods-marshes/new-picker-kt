package io.github.woods_marshes.base.builder

import io.github.woods_marshes.base.contentresolver.ContentResolverColumn
import io.github.woods_marshes.base.contentresolver.Order
import kotlinx.serialization.Serializable

@Serializable
data class Ordering(
    val column: ContentResolverColumn,
    val order: Order = Order.Descending
) {
    override fun toString(): String {
        return "${column.columnName} ${order.sqlKeyword}"
    }
}
