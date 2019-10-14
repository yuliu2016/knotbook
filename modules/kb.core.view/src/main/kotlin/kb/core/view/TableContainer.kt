package kb.core.view

import javafx.scene.input.KeyCode
import kb.core.fx.*
import kb.core.icon.icon
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetView
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.util.*

class TableContainer {

    val spreadsheet = SpreadsheetView().apply {
        hgrow()
        contextMenu = contextMenu {
            modify {
                item {
                    shortcut(KeyCode.X, control = true)
                    name("Cut")
                    icon(MDI_CONTENT_CUT, 14)
                }
                item {
                    shortcut(KeyCode.C, control = true)
                    name("Copy")
                    icon(MDI_CONTENT_COPY, 14)
                }
                item {
                    shortcut(KeyCode.C, control = true, shift = true)
                    name("Copy Special")
                }
                item {
                    shortcut(KeyCode.V, control = true)
                    name("Paste")
                    icon(MDI_CONTENT_PASTE, 14)
                }
                item {
                    shortcut(KeyCode.V, control = true, shift = true)
                    name("Paste Special")
                }
                item {
                    name("Delete")
                }
                separator()
                item {
                    shortcut(KeyCode.F, control = true)
                    name("Find and Replace")
                }
                separator()
                item {
                    shortcut(KeyCode.PLUS, control = true)
                    name("Zoom In")
                    icon(MDI_MAGNIFY_PLUS, 14)
                }
                item {
                    shortcut(KeyCode.MINUS, control = true)
                    name("Zoom Out")
                    icon(MDI_MAGNIFY_MINUS, 14)
                }
                item {
                    shortcut(KeyCode.DIGIT0, control = true)
                    name("Reset Zoom")
                }
            }
        }
        val a = grid as GridBase
        a.setRowHeightCallback { 20.0 }
        a.setResizableRows(BitSet())
        columns.forEach {
            it.setPrefWidth(84.0)
            it.minWidth = 42.0
        }
        grid.rows.first().forEach {
            it.style = "-fx-font-weight:bold; -fx-alignment: CENTER"
        }
        fixedRows.add(0)
    }
}