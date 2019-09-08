package kb.core.data

/**
 * DataFrame based on Krangl
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class DataFrame(vararg cols: DataColumn) {

    val columns: MutableList<DataColumn> = mutableListOf()

    init {
        columns.addAll(cols)
    }
}