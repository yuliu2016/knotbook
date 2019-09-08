package krangl

class IntCol(name: String, val values: Array<Int?>) : NumberCol(name) {

    constructor(name: String, values: List<Int?>) : this(name, values.toTypedArray())

    @Suppress("UNCHECKED_CAST")
    constructor(name: String, values: IntArray) : this(name, values.toTypedArray() as Array<Int?>)

    // does not work because of signature clash
    // constructor(name: String, vararg values: Int?) : this(name, values.asList().toTypedArray())

    override fun values(): Array<Int?> = values

    override val length = values.size


    override fun plusInternal(something: Any): DataCol = genericIntOp(something, { a, b -> a + b }) { a, b -> a + b }
    override fun minusInternal(something: Any): DataCol = genericIntOp(something, { a, b -> a - b }) { a, b -> a - b }
    override fun timesInternal(something: Any): DataCol = genericIntOp(something, { a, b -> a * b }, { a, b -> a * b })
    override fun divInternal(something: Any): DataCol = doubleOp(something) { a, b -> a / b }


    private fun genericIntOp(something: Any, intOp: (Int, Int) -> Int, doubleOp: (Double, Double) -> Double): DataCol {
        return when (something) {
            is IntCol -> intOp(something, intOp)
            is DoubleCol -> doubleOp(something, doubleOp)

            is Int -> this.intOp(something, intOp)
            is Double -> this.doubleOp(something, doubleOp)


            else -> throw UnsupportedOperationException()
        }
    }


    private fun doubleOp(something: Any, op: (Double, Double) -> Double): DataCol = when (something) {
        is DoubleCol -> Array(values.size) { it -> naAwareOp(this.values[it]?.toDouble(), something.values[it], op) }
        is Double -> Array(values.size) { naAwareOp(values[it]?.toDouble(), something, op) }

        else -> throw UnsupportedOperationException()
    }.let { ArrayUtils.handleArrayErasure(tempColumnName(), it) }


    private fun intOp(something: Any, op: (Int, Int) -> Int): DataCol = when (something) {
        is IntCol -> Array(values.size) { it -> naAwareOp(this.values[it], something.values[it], op) }
        is Int -> Array(values.size) { naAwareOp(values[it], something, op) }

        else -> throw UnsupportedOperationException()
    }.let { ArrayUtils.handleArrayErasure(tempColumnName(), it) }
}