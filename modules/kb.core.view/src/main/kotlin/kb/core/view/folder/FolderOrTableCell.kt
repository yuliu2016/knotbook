package kb.core.view.folder

import javafx.geometry.Pos
import javafx.scene.control.TreeCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import kb.core.fx.*

class FolderOrTableCell : TreeCell<FolderOrTable>() {

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

    override fun updateItem(item: FolderOrTable?, empty: Boolean) {
        super.updateItem(item, empty)

        if (item == null || empty) {
            graphic = null
            setOnMouseClicked {
            }
        } else {
            alignment = Pos.CENTER_LEFT
            graphic = hbox {
                alignment = Pos.CENTER_LEFT
                add(label {
                    text = item.name
                })
            }
        }

    }
}