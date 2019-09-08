package krangl

class LongCol(name: String, val values: Array<Long?>) : NumberCol(name) {

    constructor(name: String, values: List<Long?>) : this(name, values.toTypedArray())

    @Suppress("UNCHECKED_CAST")
    constructor(name: String, values: LongArray) : this(name, values.toTypedArray() as Array<Long?>)

    // does not work because of signature clash
    // constructor(name: String, vararg values: Int?) : this(name, values.asList().toTypedArray())

    override fun values(): Array<Long?> = values

    override val length = values.size


    override fun plusInternal(something: Any): DataCol = genericLongOp(something, { a, b -> a + b }) { a, b -> a + b }
    override fun minusInternal(something: Any): DataCol = genericLongOp(something, { a, b -> a - b }) { a, b -> a - b }
    override fun timesInternal(something: Any): DataCol = genericLongOp(something, { a, b -> a * b }, { a, b -> a * b })
    override fun divInternal(something: Any): DataCol = doubleOp(something, { a, b -> a / b })


    private fun genericLongOp(something: Any, longOp: (Long, Long) -> Long, doubleOp: (Double, Double) -> Double): DataCol {
        return when (something) {
            is IntCol -> longOp(something, longOp)
            is LongCol -> longOp(something, longOp)
            is DoubleCol -> doubleOp(something, doubleOp)

            is Int -> longOp(something, longOp)
            is Long -> longOp(something, longOp)
            is Double -> this.doubleOp(something, doubleOp)


            else -> throw UnsupportedOperationException()
        }
    }


    private fun doubleOp(something: Any, op: (Double, Double) -> Double): DataCol = when (something) {
        is DoubleCol -> Array(values.size) { it -> naAwareOp(this.values[it]?.toDouble(), something.values[it], op) }
        is Double -> Array(values.size, { naAwareOp(values[it]?.toDouble(), something, op) })

        else -> throw UnsupportedOperationException()
    }.let { ArrayUtils.handleArrayErasure(tempColumnName(), it) }


    private fun longOp(something: Any, op: (Long, Long) -> Long): DataCol = when (something) {
        is LongCol -> Array(values.size) { it -> naAwareOp(this.values[it], something.values[it], op) }
        is Long -> Array(values.size, { naAwareOp(values[it], something, op) })
        is IntCol -> Array(values.size) { it -> naAwareOp(this.values[it], something.values[it]?.toLong(), op) }
        is Int -> Array(values.size, { naAwareOp(values[it], something.toLong(), op) })

        else -> throw UnsupportedOperationException()
    }.let { ArrayUtils.handleArrayErasure(tempColumnName(), it) }
}