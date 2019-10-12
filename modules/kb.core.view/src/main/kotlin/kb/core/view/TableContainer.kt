package kb.core.view

import kb.core.fx.add
import kb.core.fx.vbox
import kb.core.fx.vgrow
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetView
import java.util.*

class TableContainer {

    val view = vbox {
        //        add(vbox {
//            padding = Insets(2.0, 4.0, 2.0, 4.0)
//            add(AutocompletionTextField().apply {
//                styleClass("formula-field")
//                entries.addAll(listOf("2019onto3", "2019onwin", "2019oncmp1", "2019cur", "2019iri"))
//            })
//        })
        add(SpreadsheetView().vgrow().apply {
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
        })
    }
}