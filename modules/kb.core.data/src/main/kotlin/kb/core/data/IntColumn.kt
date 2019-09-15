package kb.core.data

@Suppress("MemberVisibilityCanBePrivate")
class IntColumn(override val name: String,
                val values: Array<Int?>) : DataColumn {
    override val size: Int
        get() = values.size
}