package kb.core.data

/**
 * DataFrame based on Krangl
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class DataFrame(cols: List<DataColumn>) {

    constructor(vararg cols: DataColumn) : this(cols.toList())

    val columns: MutableList<DataColumn> = mutableListOf()

    init {
        columns.addAll(cols)
    }
}