package knotbook.core.table


// DEBUG FUNCTIONS

val DoubleArray.debug get() = joinToString(",", "[", "]")

operator fun Any.not() {
    println(this)
}