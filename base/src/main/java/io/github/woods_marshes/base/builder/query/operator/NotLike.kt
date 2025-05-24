package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun NotLike(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.NotLike, suffixOperand)

fun foldNotLike(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> NotLike(acc, o2) }

fun Expression.notLike(initializer: Expression.() -> Unit) {
    addOperand(foldNotLike(*Expression().apply(initializer).conditions.toTypedArray()))
}