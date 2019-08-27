@file:Suppress("unused")

package knotbook.bowline


// DEBUG FUNCTIONS

val DoubleArray.debug get() = joinToString(",", "[", "]")

operator fun Any.not() {
    println(this)
}