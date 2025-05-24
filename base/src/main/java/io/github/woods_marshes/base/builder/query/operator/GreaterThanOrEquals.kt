package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun GreaterThanOrEquals(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.GreaterThanOrEquals, suffixOperand)

fun foldGreaterThanOrEquals(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> Or(acc, o2) }

fun Expression.greaterThanOrEquals(initializer: Expression.() -> Unit) {
    addOperand(foldGreaterThanOrEquals(*Expression().apply(initializer).conditions.toTypedArray()))
}