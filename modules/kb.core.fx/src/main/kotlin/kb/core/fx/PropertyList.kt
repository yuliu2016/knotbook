package kb.core.fx

import javafx.event.EventHandler
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.util.Callback


@Suppress("unused")
class PropertyList(vararg initialItems: String) : ListView<String>(initialItems.toMutableList().observable()) {

    private inner class Cell : ListCell<String>() {

        init {
            onDragDetected = EventHandler { event ->
                if (item == null) {
                    return@EventHandler
                }
                val board = startDragAndDrop(TransferMode.MOVE)
                val content = ClipboardContent()
                content.putString(item)
                board.setContent(content)
                event.consume()
            }

            onDragOver = EventHandler { event ->
                if (event.gestureSource !== this@PropertyList && event.dragboard.hasString() && !isEmpty) {
                    event.acceptTransferModes(TransferMode.MOVE)
                }
                event.consume()
            }

            onDragEntered = EventHandler { event ->
                if (event.gestureSource !== this@PropertyList && event.dragboard.hasString() && !isEmpty) {
                    styleClass("drag-over")
                }
            }

            onDragExited = EventHandler { event ->
                if (event.gestureSource !== this@PropertyList && event.dragboard.hasString() && !isEmpty) {
                    noStyleClass()
                }
            }

            onDragDropped = EventHandler { event ->
                if (item == null) {
                    return@EventHandler
                }

                val db = event.dragboard
                var success = false

                if (db.hasString()) {
                    val items = listView.items
                    val draggedIdx = items.indexOf(db.string)
                    val thisIdx = items.indexOf(item)

                    items[draggedIdx] = item
                    items[thisIdx] = db.string

                    success = true
                }

                event.isDropCompleted = success

                event.consume()
            }

            onDragDone = EventHandler { it.consume() }
        }

        override fun updateItem(item: String?, empty: Boolean) {
            super.updateItem(item, empty)
            text = if (empty || item == null) null else "${index + 1}. $item"
        }
    }

    init {

        styleClass("properties-list")

        focusedProperty().addListener { _, _, newValue ->
            if (!newValue) {
                selectionModel.clearSelection()
            }
        }

        cellFactory = Callback {
            Cell()
        }

        onScroll = EventHandler {
            // consume the event so it doesn't pass to parent (which may also be scrolling
            it.consume()
        }
    }
}