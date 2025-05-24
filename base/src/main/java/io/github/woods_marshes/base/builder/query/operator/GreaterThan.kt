package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun GreaterThan(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.GreaterThan, suffixOperand)

fun foldGreaterThan(vararg operands: Operand): Operand =
    operands.foldFromFirst { acc, o2 -> GreaterThan(acc, o2) }

fun Expression.greaterThan(initializer: Expression.() -> Unit) {
    addOperand(foldGreaterThan(*Expression().apply(initializer).conditions.toTypedArray()))
}