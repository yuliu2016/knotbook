package kb.core.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import kb.core.fx.*

class TableContainer {

    private val table = TableView<Int>()

    init {
        table.items = (0..100).toList().observable()

        table.columns.addAll((0..10).map { col ->
            TableColumn<Int, String>(col.toString()).apply {
                this.setCellValueFactory {
                    SimpleStringProperty((col * it.value).toString())
                }
                this.prefWidth = 84.0
                isSortable = false
            }
        })
        table.fixedCellSize = Region.USE_COMPUTED_SIZE
        table.selectionModel.isCellSelectionEnabled = true
        table.selectionModel.selectionMode = SelectionMode.MULTIPLE

        table.focusedProperty().addListener { _, _, newValue ->
            if (!newValue) {
                table.selectionModel.clearSelection()
            }
        }
    }

    val view = vbox {
        add(hbox {
            prefHeight = 20.0
            align(Pos.CENTER)
            add(Label("Table Folder/"))
            add(Label("Table Name").apply {
                textFill = Color.BLUE
            })
            padding = Insets(0.0, 4.0, 0.0, 4.0)
        })
        add(AutocompletionTextField().apply {
            entries.addAll(listOf("2019onto3", "2019onwin", "2019oncmp1", "2019cur", "2019iri"))
        })
        add(table.vgrow())
    }
}