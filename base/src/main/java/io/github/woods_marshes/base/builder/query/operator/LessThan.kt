package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun LessThan(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.LessThan, suffixOperand)

fun foldLessThan(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> LessThan(acc, o2) }

fun Expression.lessThan(initializer: Expression.() -> Unit) {
    addOperand(foldLessThan(*Expression().apply(initializer).conditions.toTypedArray()))
}