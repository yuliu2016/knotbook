package knotbook.formulas.function

import knotbook.formulas.token.Expr
import knotbook.formulas.token.Num
import solve

class FunctionFormula(val f: (List<Double>) -> Double) : Formula() {
    override fun solve(args: List<Expr>): Expr {
        return listOf(Num(f(args.map { it.solve() })))
    }
}
