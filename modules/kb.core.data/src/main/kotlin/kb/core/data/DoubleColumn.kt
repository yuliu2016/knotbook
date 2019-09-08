package kb.core.data

class DoubleColumn(
        override val name: String,
        val values: DoubleArray = doubleArrayOf()
) : DataColumn {
}