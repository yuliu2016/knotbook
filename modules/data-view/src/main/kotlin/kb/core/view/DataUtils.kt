@file:Suppress("unused")

package kb.core.view

import kb.core.fx.observable
import kb.core.view.util.CellBase2
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

data class RGB(val r: Int, val g: Int, val b: Int)

data class ColorScale(val index: Int, val sortType: SortType, val rgb: RGB) {
    override fun equals(other: Any?): Boolean {
        return other is ColorScale && other.index == index
    }

    override fun hashCode(): Int {
        return index
    }

    companion object {
        val green = RGB(96, 192, 144)
        val orange = RGB(255, 144, 0)
        val blue = RGB(100, 170, 255)
        val red = RGB(255, 108, 108)
    }
}

fun RGB.blendStyle(alpha: Double, bg: Int): String {
    val r2 = (alpha * r + (1 - alpha) * bg).toInt()
    val g2 = (alpha * g + (1 - alpha) * bg).toInt()
    val b2 = (alpha * b + (1 - alpha) * bg).toInt()
    return "-fx-background-color: rgb($r2,$g2,$b2)"
}

fun TableArray.toGrid(): GridBase {
    val rows = rows
    val cols = cols
    val grid = GridBase(rows, cols)
    grid.setRowHeightCallback { 20.0 }
    grid.setResizableRows(BitSet())
    grid.rows.addAll((0 until rows).map { row ->
        (0 until cols).map { col ->
            val cell = CellBase2(row, col, getString(row, col))
            if (this.isNumber(row, col) && rows < 1000) {
                cell.styleClass.add("num-cell")
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