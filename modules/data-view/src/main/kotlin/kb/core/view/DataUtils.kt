@file:Suppress("unused")

package kb.core.view

import kb.core.fx.observable
import kb.service.api.array.TableArray
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetCellType
import org.controlsfx.control.spreadsheet.SpreadsheetView
import java.util.*

data class SortColumn(val sortType: SortType, val index: Int) {
    override fun equals(other: Any?): Boolean {
        return other is SortColumn && other.index == index
    }

    override fun hashCode(): Int {
        return index
    }
}

enum class SortType {
    Ascending, Descending
}

data class ColorScale(val index: Int, val sortType: SortType, val r: Int, val g: Int, val b: Int) {
    override fun equals(other: Any?): Boolean {
        return other is ColorScale && other.index == index
    }

    override fun hashCode(): Int {
        return index
    }

    companion object {
        val green = ColorScale(0, SortType.Ascending, 96, 192, 144)
        val orange = ColorScale(0, SortType.Ascending, 255, 144, 0)
        val blue = ColorScale(0, SortType.Ascending, 100, 170, 255)
        val read = ColorScale(0, SortType.Ascending, 255, 108, 108)
    }
}

fun TableArray.toGrid(): GridBase {
    val rows = rows
    val cols = cols
    val grid = GridBase(rows, cols)
    grid.setRowHeightCallback { 20.0 }
    grid.setResizableRows(BitSet())
    grid.rows.addAll((0 until rows).map { row ->
        (0 until cols).map { col ->
            val cell = SpreadsheetCellType.STRING.createCell(row, col, 1, 1, getString(row, col))
            if (this.isNumber(row, col)) {
                cell.style = "-fx-alignment: CENTER-RIGHT"
            }
            cell
        }.observable()
    })
    grid.rows.first().forEach { c -> c.styleClass.add("header-cell") }
    return grid
}

fun emptyGrid(): GridBase {
    val grid = GridBase(24, 12)
    grid.setRowHeightCallback { 20.0 }
    grid.setResizableRows(BitSet())
    grid.rows.addAll((0 until 24).map { row ->
        (0 until 12).map { col ->
            val cell = SpreadsheetCellType.STRING.createCell(row, col, 1, 1, "")
            cell
        }.observable()
    })
    return grid
}

fun SpreadsheetView.getSelection(): Selection {
    val selection = Selection()
    for (p in selectionModel.selectedCells) {
        val modelRow = getModelRow(p.row)
        val modelCol = getModelColumn(p.column)
        selection.rows.add(modelRow)
        selection.cols.add(modelCol)
    }
    return selection
}

data class Selection(
        val rows: MutableSet<Int> = mutableSetOf(),
        val cols: MutableSet<Int> = mutableSetOf()
) {
    val minRow get() = rows.min() ?: 0
    val maxRow get() = rows.max() ?: 0
    val minCol get() = cols.min() ?: 0
    val maxCol get() = cols.max() ?: 0
}