package io.github.woods_marshes.base.builder.query.operator

import io.github.woods_marshes.base.builder.query.BinaryOperatorExpression
import io.github.woods_marshes.base.builder.query.Expression
import io.github.woods_marshes.base.builder.query.operand.Operand
import io.github.woods_marshes.base.utils.foldFromFirst

@Suppress("FunctionName")
fun Equal(prefixOperand: Operand, suffixOperand: Operand) =
    BinaryOperatorExpression(prefixOperand, Operator.Equal, suffixOperand)

fun foldEqual(vararg operands: Operand): Operand = operands.foldFromFirst { acc, o2 -> Equal(acc, o2) }

fun Expression.equal(initializer: Expression.() -> Unit) {
    addOperand(foldEqual(*Expression().apply(initializer).conditions.toTypedArray()))
}