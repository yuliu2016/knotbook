@file:Suppress("unused")

package kb.core.bowline


// DEBUG FUNCTIONS

val DoubleArray.debug get() = joinToString(",", "[", "]")

operator fun Any.not() {
    println(this)
}