package kb.plugin.appscanner

import javafx.scene.control.TableCell
import kb.core.fx.label

class TeamCell : TableCell<V5Entry, String>() {
    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item == null || empty) {
            graphic = null
            return
        }
        graphic = label {
            text = item
            val entry = tableRow?.item
            if (entry != null) {
                style = when (entry.board.alliance) {
                    Alliance.Red -> "-fx-font-weight: bold; -fx-text-fill: red"
                    Alliance.Blue -> "-fx-font-weight: bold; -fx-text-fill: blue"
                }
            }
        }
    }
}

class MatchCell : TableCell<V5Entry, String>() {
    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item == null || empty) {
            graphic = null
            return
        }
        graphic = label {
            text = item
            style = when (item.last()) {
                '1', '3', '5', '7', '9' -> "-fx-font-weight: bold; -fx-text-fill: #080"
                else -> "-fx-font-weight: bold; -fx-text-fill: #808 "
            }
        }
    }
}