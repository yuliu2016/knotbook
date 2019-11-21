package kb.core.view.app

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import kb.core.fx.*
import kb.service.api.ui.OptionItem

class OptionItemCell : ListCell<OptionItem>() {

    private val textBox = hbox {
        align(Pos.CENTER_LEFT)
        hgrow()
    }

    private val graphicBox = hbox {
        align(Pos.CENTER)
        prefWidth = 24.0
    }

    override fun updateItem(item: OptionItem?, empty: Boolean) {
        super.updateItem(item, empty)
        prefHeight = 24.0
        if (item == null || empty) {
            graphic = null
            return
        }

        textBox.children.clear()
        val name = if (item.name.length > 50) item.name.substring(0, 47) + "..." else item.name

        if (item.highlight == null || item.highlight.isEmpty()) {
            textBox.add(Label(name))
        } else {
            val ix = item.highlight
            var highlighted = ix[0]
            var i = 0 // start index
            for (j in name.indices) {
                if (ix[j]) {
                    if (!highlighted) {
                        highlighted = true
                        textBox.add(Label(name.substring(i, j)))
                        i = j
                    }
                } else if (highlighted) {
                    highlighted = false
                    textBox.add(Label(name.substring(i, j)).apply {
                        styleClass("list-highlight")
                    })
                    i = j
                }
            }
            if (highlighted) {
                textBox.add(label(name.substring(i)).styleClass("list-highlight"))
            } else {
                textBox.add(label(name.substring(i)))
            }
        }
        graphicBox.children.clear()
        if (item.graphic != null) {
            graphicBox.children.add(item.graphic)
        }
        graphic = hbox {
            alignment = Pos.CENTER_LEFT
            padding = Insets(0.0, 24.0, 0.0, 8.0)
            spacing = 4.0
            add(graphicBox)
            add(textBox)
            if (item.info != null) {
                add(label(item.info).styleClass("list-info"))
            }
        }
        alignment = Pos.CENTER_LEFT
    }
}