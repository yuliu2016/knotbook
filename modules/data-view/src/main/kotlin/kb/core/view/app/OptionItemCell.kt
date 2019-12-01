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

    private val info1 = label("").styleClass("list-info")
    private val info2 = label("")

    private val cellContainer = hbox {
        alignment = Pos.CENTER_LEFT
        padding = Insets(0.0, 24.0, 0.0, 8.0)
        spacing = 4.0
        add(graphicBox)
        add(textBox)
        add(info1)
        add(info2)
    }

    private val labelPile = mutableListOf<Label>()
    private var pileIndex = 0

    private fun labelFromPile(s: String, bold: Boolean): Label {
        val theLabel = if (pileIndex < labelPile.size) {
            labelPile[pileIndex]
        } else {
            val newLabel = label("")
            labelPile.add(newLabel)
            newLabel
        }
        theLabel.text = s
        pileIndex++
        theLabel.styleClass.remove("list-highlight")
        if (bold) {
            theLabel.styleClass.add("list-highlight")
        }
        return theLabel
    }

    override fun updateItem(item: OptionItem?, empty: Boolean) {
        super.updateItem(item, empty)
        prefHeight = 22.0
        if (item == null || empty) {
            graphic = null
            return
        }

        textBox.children.clear()
        val name = if (item.name.length > 50) item.name.substring(0, 47) + "..." else item.name

        pileIndex = 0
        if (item.highlight == null || item.highlight.isEmpty()) {
            textBox.add(labelFromPile(name, false))
        } else {
            val ix = item.highlight
            var highlighted = ix[0]
            var i = 0 // start index
            for (j in name.indices) {
                if (ix[j]) {
                    if (!highlighted) {
                        highlighted = true
                        textBox.add(labelFromPile(name.substring(i, j), false))
                        i = j
                    }
                } else if (highlighted) {
                    highlighted = false
                    textBox.add(labelFromPile(name.substring(i, j), true))
                    i = j
                }
            }
            textBox.add(labelFromPile(name.substring(i), highlighted))
        }
        graphicBox.children.clear()
        if (item.graphic != null) {
            graphicBox.children.add(item.graphic)
        }

        if (item.info1 != null) {
            info1.text = item.info1
            info1.isVisible = true
        } else {
            info1.text = null
            info1.isVisible = false
        }
        if (item.info2 != null) {
            info2.text = item.info2
            info2.isVisible = true
        } else {
            info2.text = null
            info2.isVisible = false
        }

        graphic = cellContainer
        alignment = Pos.CENTER_LEFT
    }
}