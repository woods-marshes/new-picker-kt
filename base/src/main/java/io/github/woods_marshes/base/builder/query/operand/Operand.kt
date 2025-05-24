package io.github.woods_marshes.base.builder.query.operand

import io.github.woods_marshes.base.contentresolver.ContentResolverColumn
import kotlinx.serialization.Serializable

interface Operand

@Serializable
open class Column(private val columnName: String, private val surroundedBy: String = "") : Operand {
    override fun toString(): String = "$surroundedBy${columnName}$surroundedBy"
}

@Serializable
class ContentColumn(
    val column: ContentResolverColumn,
) : Column(columnName = column.columnName, surroundedBy = "")