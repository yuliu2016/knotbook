package kb.core.view

import javafx.geometry.Pos
import javafx.scene.control.TreeCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import kb.core.fx.*
import kb.core.icon.centered

class EntityCell : TreeCell<Entity>() {

    init {
        setOnDragDetected { event ->
            if (item == null || !treeItem.isLeaf) {
                return@setOnDragDetected
            }
            val board = startDragAndDrop(TransferMode.MOVE)
            val content = ClipboardContent()
            content.putString(item.hashCode().toString())
            board.setContent(content)
            event.consume()
        }

        setOnDragOver { event ->
            if (event.gestureSource !== this && event.dragboard.hasString() && !isEmpty) {
                event.acceptTransferModes(TransferMode.MOVE)
            }
            event.consume()
        }

        setOnDragEntered { event ->
            if (event.gestureSource !== this && event.dragboard.hasString() && !isEmpty) {
                styleClass("drag-over")
            }
        }

        setOnDragExited { event ->
            if (event.gestureSource !== this && event.dragboard.hasString() && !isEmpty) {
                noStyleClass()
            }
        }

        setOnDragDropped { event ->
            if (item == null) {
                return@setOnDragDropped
            }

            val db = event.dragboard
            var success = false

            if (db.hasString()) {
                success = true
            }

            event.isDropCompleted = success

            event.consume()
        }

        setOnDragDone { it.consume() }
    }

    override fun updateItem(item: Entity?, empty: Boolean) {
        super.updateItem(item, empty)

        if (item == null || empty) {
            graphic = null
            setOnMouseClicked {
            }
        } else {
            alignment = Pos.CENTER_LEFT
            graphic = hbox {
                alignment = Pos.CENTER_LEFT
                add(item.icon.centered(20))
                add(label {
                    text = item.text
                    alignment = Pos.CENTER_LEFT
                    if (item.color != null) {
                        textFill = item.color
                    }
                    style = "-fx-font-weight:bold"
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