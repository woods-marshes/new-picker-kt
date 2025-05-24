package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun In(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.In, suffixOperand)

fun foldIn(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> In(acc, o2) }

fun Expression.includedIn(initializer: Expression.() -> Unit) {
    addOperand(foldIn(*Expression().apply(initializer).conditions.toTypedArray()))
}