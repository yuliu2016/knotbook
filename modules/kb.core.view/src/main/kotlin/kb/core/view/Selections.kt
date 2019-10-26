package ca.warp7.rt.view.window

import ca.warp7.rt.view.api.Selection
import org.controlsfx.control.spreadsheet.SpreadsheetView

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