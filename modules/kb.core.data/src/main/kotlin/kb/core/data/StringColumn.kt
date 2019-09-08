package kb.core.data

class StringColumn(
        override val name: String,
        val values: Array<String> = arrayOf()
): DataColumn