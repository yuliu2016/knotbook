package ca.warp7.rt.view.api

data class Selection(
        val rows: MutableSet<Int> = mutableSetOf(),
        val cols: MutableSet<Int> = mutableSetOf()
) {
    val minRow get() = rows.min() ?: 0
    val maxRow get() = rows.max() ?: 0
    val minCol get() = cols.min() ?: 0
    val maxCol get() = cols.max() ?: 0
}