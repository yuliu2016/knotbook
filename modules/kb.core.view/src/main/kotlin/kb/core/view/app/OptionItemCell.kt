package kb.core.view.app

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ListCell
import kb.core.fx.*
import kb.service.api.ui.OptionItem

class OptionItemCell : ListCell<OptionItem>() {

    override fun updateItem(item: OptionItem?, empty: Boolean) {
        super.updateItem(item, empty)

        prefHeight = 24.0
        if (item == null || empty) {
            graphic = null
        } else {
            val ix = item.highlight
            graphic = hbox {
                alignment = Pos.CENTER_LEFT
                padding = Insets(0.0, 8.0, 0.0, 8.0)
                spacing = 45.0
                if (item.graphic != null) {
                    add(item.graphic.centered(24))
                } else {
                    add(hbox {
                        prefWidth = 24.0
                    })
                }
                add(textFlow {
                    var j = 0
                    var hl = false
                    for (i in item.name.indices) {
                        if (j < ix.size) {

                        }
                    }
                })
                add(label {
                    text = item.name
                    alignment = Pos.CENTER_LEFT
                    styleClass("list-highlight")
                })
                add(label {
                    text = item.name
                })
            }
            alignment = Pos.CENTER_LEFT
        }
    }
}