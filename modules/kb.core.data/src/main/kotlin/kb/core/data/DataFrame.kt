package kb.core.data

/**
 * DataFrame based on Krangl
 */
@Suppress("MemberVisibilityCanBePrivate", "unused", "CanBeParameter")
class DataFrame(val columns: List<DataColumn>) {

    constructor(vararg cols: DataColumn) : this(cols.toList())

    val ncol = columns.size
    val nrow = columns.asSequence().map { it.size }.max() ?: 0
}