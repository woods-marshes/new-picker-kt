package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun NotEqual(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.NotEqual, suffixOperand)

fun foldNotEqual(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> NotEqual(acc, o2) }

fun Expression.notEqual(initializer: Expression.() -> Unit) {
    addOperand(foldNotEqual(*Expression().apply(initializer).conditions.toTypedArray()))
}