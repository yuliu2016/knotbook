package kb.core.view.app

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import kb.core.fx.*
import kb.service.api.ui.OptionItem

class OptionItemCell : ListCell<OptionItem>() {

    override fun updateItem(item: OptionItem?, empty: Boolean) {
        super.updateItem(item, empty)
        prefHeight = 24.0
        if (item == null || empty) {
            graphic = null
            return
        }

        val t = hbox {
            align(Pos.CENTER_LEFT)
            hgrow()
        }

        if (item.highlight == null || item.highlight.isEmpty()) {
            t.add(Label(item.name))
        } else {
            val ix = BooleanArray(item.name.length)
            for (i in item.highlight) {
                ix[i] = true
            }
            var highlighted = ix[0]
            var i = 0 // start index
            for (j in item.name.indices) {
                if (ix[j]) {
                    if (!highlighted) {
                        highlighted = true
                        t.add(Label(item.name.substring(i, j + 1)))
                        i = j + 1
                    }
                } else if (highlighted) {
                    highlighted = false
                    t.add(Label(item.name.substring(i, j + 1)).apply {
                        styleClass("list-highlight")
                    })
                    i = j + 1
                }
            }
            if (highlighted) {
                t.add(Label(item.name.substring(i)).apply {
                    styleClass("list-highlight")
                })
            } else {
                t.add(Label(item.name.substring(i)))
            }
        }
        graphic = hbox {
            alignment = Pos.CENTER_LEFT
            padding = Insets(0.0, 24.0, 0.0, 8.0)
            spacing = 4.0
            add(item.graphic.centered(24))
            add(t)
            if (item.info != null) {
                add(label(item.info))
            }
        }
        alignment = Pos.CENTER_LEFT
    }
}