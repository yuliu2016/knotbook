package krangl

class BooleanCol(name: String, val values: Array<Boolean?>) : DataCol(name) {
    constructor(name: String, values: List<Boolean?>) : this(name, values.toTypedArray())

    override fun not(): DataCol {
        return BooleanCol(name, values.map { it?.not() })
    }

    override fun values(): Array<Boolean?> = values

    override val length = values.size
}