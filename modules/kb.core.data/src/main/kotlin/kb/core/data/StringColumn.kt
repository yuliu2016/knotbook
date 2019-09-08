package kb.core.data

@Suppress("MemberVisibilityCanBePrivate")
class StringColumn(
        override val name: String,
        val values: Array<String> = arrayOf()
): DataColumn {
    override val size: Int
        get() = values.size
}