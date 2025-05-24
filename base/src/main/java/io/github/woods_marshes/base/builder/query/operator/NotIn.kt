package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun NotIn(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.NotIn, suffixOperand)

fun foldNotIn(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> NotIn(acc, o2) }

fun Expression.notIncludedIn(initializer: Expression.() -> Unit) {
    addOperand(foldNotIn(*Expression().apply(initializer).conditions.toTypedArray()))
}