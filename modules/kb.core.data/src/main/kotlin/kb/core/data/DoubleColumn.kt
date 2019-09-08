package kb.core.data

@Suppress("MemberVisibilityCanBePrivate")
class DoubleColumn(
        override val name: String,
        val values: DoubleArray = doubleArrayOf()
) : DataColumn {

    override val size: Int
        get() = values.size
}