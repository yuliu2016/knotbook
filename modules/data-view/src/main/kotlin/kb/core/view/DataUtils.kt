@file:Suppress("unused")

package kb.core.view

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.TablePosition
import javafx.scene.paint.Color
import kb.core.fx.observable
import kb.core.view.util.CellBase2
import kb.service.api.array.TableArray
import kb.service.api.array.Tables
import kb.service.api.ui.RGB
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetView
import org.kordamp.ikonli.Ikon
import java.util.*

data class SortColumn(val index: Int, val sortType: SortType) {
    override fun equals(other: Any?): Boolean {
        return other is SortColumn && other.index == index
    }

    override fun hashCode(): Int {
        return index
    }
}

data class Tab(
        val title: String,
        val icon: Ikon? = null,
        val iconColor: Color = Color.GRAY,
        val data: TableArray? = null,
        val placeholder: Node? = null
)

enum class SortType {
    Ascending, Descending
}

data class ColorScale(val index: Int, val sortType: SortType, val rgb: RGB) {
    override fun equals(other: Any?): Boolean {
        return other is ColorScale && other.index == index
    }

    override fun hashCode(): Int {
        return index
    }

}

object PresetCS {
    val green = RGB(96, 192, 144)
    val orange = RGB(255, 144, 0)
    val blue = RGB(100, 170, 255)
    val red = RGB(255, 108, 108)
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
            if (this.isNumber(row, col)) {
                cell.styleClass.add("num-cell")
            }
            cell
        }.observable()
    })
    grid.rows.first().forEach { c -> c.styleClass.add("header-cell") }
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

fun getRangeText(a: ObservableList<TablePosition<Any?, Any?>>): String {
    if (a.isEmpty()) return "None"
    val rows = a.map { it.row }
    val cols = a.map { it.column }
    val w = rows.min()!! + 1
    val x = rows.max()!! + 1
    val y = Tables.columnIndexToString(cols.min()!!)
    val z = Tables.columnIndexToString(cols.max()!!)
    return if (a.size == 1) "$y$w" else "$y$w:$z$x"
}

fun getCalculations(a: ObservableList<TablePosition<Any?, Any?>>, array: TableArray): String {
    var count = 0
    var sum = 0.0
    var min = Double.MAX_VALUE
    var max = Double.MIN_VALUE
    for (pos in a) {
        val num = array[pos.row, pos.column]
        if (num.isFinite()) {
            count++
            sum += num
            if (num < min) min = num
            if (num > max) max = num
        }
    }
    if (count == 0) {
        return ""
    }
    val average = (sum / count).toFloat() // make it a shorter string
    return "Sum: $sum    Count: $count    Average: $average    Min: $min    Max: $max"
}