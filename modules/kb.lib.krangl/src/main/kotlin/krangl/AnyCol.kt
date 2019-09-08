package krangl

class AnyCol(name: String, val values: Array<Any?>) : DataCol(name) {
    constructor(name: String, values: List<Any?>) : this(name, values.toTypedArray<Any?>())

    override fun values(): Array<Any?> = values

    override val length = values.size
}