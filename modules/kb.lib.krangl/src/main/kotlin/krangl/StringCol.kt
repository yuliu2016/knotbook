package krangl

class StringCol(name: String, val values: Array<String?>) : DataCol(name) {

    constructor(name: String, values: List<String?>) : this(name, values.toTypedArray())

    override fun values(): Array<String?> = values

    override val length = values.size


    override fun plusInternal(something: Any): DataCol = when (something) {
        is DataCol -> Array(values.size) { values[it] }.mapIndexed { index, rowVal ->
            naAwarePlus(rowVal, something.values()[index]?.toString())
        }
        else -> throw UnsupportedOperationException()
    }.let {
        StringCol(tempColumnName(), it)
    }

    internal fun naAwarePlus(first: String?, second: String?): String? {
        return if (first == null || second == null) null else first + second
    }
}