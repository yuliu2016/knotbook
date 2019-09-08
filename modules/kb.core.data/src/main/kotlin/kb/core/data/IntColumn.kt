package kb.core.data

class IntColumn(
        override val name: String,
        val values: IntArray = intArrayOf()
): DataColumn