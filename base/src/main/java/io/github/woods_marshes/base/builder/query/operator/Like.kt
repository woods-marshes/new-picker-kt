package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun Like(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.Like, suffixOperand)

fun foldLike(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> Like(acc, o2) }

fun Expression.like(initializer: Expression.() -> Unit) {
    addOperand(foldLike(*Expression().apply(initializer).conditions.toTypedArray()))
}