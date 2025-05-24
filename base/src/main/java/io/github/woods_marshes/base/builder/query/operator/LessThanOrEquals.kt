package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun LessThanOrEquals(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.LessThanOrEquals, suffixOperand)

fun foldLessThanOrEquals(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> LessThanOrEquals(acc, o2) }

fun Expression.lessThanOrEquals(initializer: Expression.() -> Unit) {
    addOperand(foldLessThanOrEquals(*Expression().apply(initializer).conditions.toTypedArray()))
}