package kb.core.view

import kb.core.fx.observable
import kb.service.api.array.TableArray
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetCellType
import java.util.*

fun TableArray.toGrid(): GridBase {
    val rows = rows
    val cols = cols
    val grid = GridBase(rows, cols)
    grid.setRowHeightCallback { 20.0 }
    grid.setResizableRows(BitSet())
    grid.rows.addAll((0 until rows).map { row ->
        (0 until cols).map { col ->
            val cell = SpreadsheetCellType.STRING.createCell(row, col, 1, 1, this[row, col])
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