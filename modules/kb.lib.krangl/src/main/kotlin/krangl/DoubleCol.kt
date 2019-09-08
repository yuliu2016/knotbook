package krangl

// https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html
// most methods are implemented it in kotlin.collections.plus
class DoubleCol(name: String, val values: Array<Double?>) : DataCol(name) {

    constructor(name: String, values: List<Double?>) : this(name, values.toTypedArray())

    @Suppress("UNCHECKED_CAST")
    constructor(name: String, values: DoubleArray) : this(name, values.toTypedArray() as Array<Double?>)

    override fun values(): Array<Double?> = values

    override val length = values.size

    override fun plusInternal(something: Any): DataCol = arithOp(something) { a, b -> a + b }
    override fun minusInternal(something: Any): DataCol = arithOp(something) { a, b -> a - b }

    override fun timesInternal(something: Any): DataCol = arithOp(something) { a, b -> a * b }
    override fun divInternal(something: Any): DataCol = arithOp(something) { a, b -> a / b }


    private fun arithOp(something: Any, op: (Double, Double) -> Double): DataCol = when (something) {
        is DoubleCol -> Array(values.size) { naAwareOp(this.values[it], something.values[it], op) }
        is IntCol -> Array(values.size) { naAwareOp(this.values[it], something.values[it]?.toDouble(), op) }
        is Number -> Array(values.size) { naAwareOp(values[it], something.toDouble(), op) }
        else -> throw UnsupportedOperationException()
    }.let { DoubleCol(tempColumnName(), it) }
}