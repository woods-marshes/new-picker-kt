package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun And(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.And, suffixOperand)

fun foldAnd(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> And(acc, o2) }

fun Expression.and(initializer: Expression.() -> Unit) {
    addOperand(foldAnd(*Expression().apply(initializer).conditions.toTypedArray()))
}