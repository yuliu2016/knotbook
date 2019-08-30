package kb.core.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TreeCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import kb.core.fx.*

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
        super.updateItem(item, empty)

        if (item == null || empty) {
            graphic = null
        } else {
            alignment = Pos.CENTER_LEFT
            graphic = hbox {
                alignment = Pos.CENTER_LEFT
                padding = Insets(0.0, 0.0, 0.0, 4.0)
                if (item.icon != null) {
                    add(item.icon.centerIn(16))
                }
                children.addAll(item.text.map {
                    label {
                        text = it.string
                        if (it.color != null) {
                            textFill = it.color
                        }
                        if (it.bold) {
                            style = "-fx-font-weight:bold"
                        }
                    }
                })

                setOnMouseClicked { event ->
                    if (event.clickCount == 2) {
                        println("hi")
                    }
                }
            }
        }
    }
}