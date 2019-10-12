package kb.core.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ListCell
import javafx.scene.control.TreeCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import kb.core.fx.*
import kb.core.icon.centered

@Suppress("DuplicatedCode")
class EntityListCell : ListCell<Entity>() {

    override fun updateItem(item: Entity?, empty: Boolean) {
        super.updateItem(item, empty)

        prefHeight = 24.0
        if (item == null || empty) {
            graphic = null
            setOnMouseClicked {
            }
        } else {
            alignment = Pos.CENTER_LEFT
            graphic = hbox {
                padding = Insets(0.0, 8.0, 0.0, 8.0)
                alignment = Pos.CENTER_LEFT
                spacing = 4.0
                add(item.icon.centered(20))
                add(label {
                    text = item.text
                    alignment = Pos.CENTER_LEFT
//                    if (item.color != null) {
//                        textFill = item.color
//                    }
//                    style = "-fx-font-weight:bold"
                })
                if (item.supportText != null) {
                    add(label {
                        text = item.supportText
                    })
                }
            }
        }
    }
}