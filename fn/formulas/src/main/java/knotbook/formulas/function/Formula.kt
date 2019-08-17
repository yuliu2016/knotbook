package knotbook.formulas.function

import knotbook.formulas.token.Expr

abstract class Formula{
    abstract fun solve(args: List<Expr>): Expr
}