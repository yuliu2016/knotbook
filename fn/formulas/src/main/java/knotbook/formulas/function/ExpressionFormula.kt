package knotbook.formulas.function

import knotbook.formulas.token.Ch
import knotbook.formulas.token.Expr
import knotbook.formulas.token.Name
import knotbook.formulas.token.Num
import replaceAll
import solve


data class ExpressionFormula(val expr: Expr, val inputArgs: List<Name>) : Formula() {
    override fun solve(args: List<Expr>): Expr {
        if (inputArgs.size != args.size) throw IllegalArgumentException("Incorrect number of arguments")

        val map: MutableMap<Name, Expr> = inputArgs.zip(args).toMap().toMutableMap()
        var solution = expr
        map.forEach { solution = solution.replaceAll(it.key, listOf(Num(it.value.solve()!!))) }

        return listOf(Ch('(')) + solution + listOf(Ch(')'))
    }
}