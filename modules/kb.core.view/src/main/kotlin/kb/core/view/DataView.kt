package kb.core.view

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleStringProperty
import javafx.scene.input.KeyCode
import javafx.stage.FileChooser
import kb.core.fx.*
import kb.core.icon.icon
import kb.core.view.app.Singleton
import kb.core.view.app.WindowBase
import kb.service.api.array.TableArray
import kb.service.api.array.TableUtil
import org.controlsfx.control.spreadsheet.SpreadsheetView
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.io.FileInputStream


@Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode", "unused")
class DataView {

    val base = WindowBase()

    fun tableFromFile() {
        val fc = FileChooser()
        fc.title = "Open Table from File"
        val f = fc.showOpenDialog(base.stage)
        if (f != null && f.extension == "csv") {
            base.docLabel.text = "Loading"
            Thread {
                try {
                    val a = TableArray.fromCSV(FileInputStream(f), true)
                    runOnFxThread {
                        base.stage.isMaximized = true
                        spreadsheet.grid = a.toGrid()
                        spreadsheet.fixedRows.setAll(0)
                        base.docLabel.text = f.absolutePath
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    val zoomText = SimpleStringProperty("100%")
    val themeText = SimpleStringProperty("Light")
    val selectionText = SimpleStringProperty("None")

    private val spreadsheet = SpreadsheetView(emptyGrid()).apply {
        selectionModel.selectedCells.addListener(InvalidationListener {
            selectionText.value = getRange()
        })
        columns.forEach {
            it.setPrefWidth(75.0)
        }
        zoomFactorProperty().addListener { _, _, nv ->
            zoomText.value = "${(nv.toDouble() * 100).toInt()}%"
        }
        hgrow()
        contextMenu = contextMenu {
            modify {
                item {
                    name("Undo")
                    shortcut(KeyCode.Z, control = true)
                    icon(MDI_UNDO, 14)
                }
                item {
                    name("Redo")
                    shortcut(KeyCode.Z, control = true, shift = true)
                    icon(MDI_REDO, 14)
                }
                separator()
                item {
                    shortcut(KeyCode.X, control = true)
                    name("Cut")
                    icon(MDI_CONTENT_CUT, 14)
                }
                item {
                    shortcut(KeyCode.C, control = true)
                    name("Copy")
                    icon(MDI_CONTENT_COPY, 14)
                }
                item {
                    shortcut(KeyCode.C, control = true, shift = true)
                    name("Copy Special")
                }
                item {
                    shortcut(KeyCode.V, control = true)
                    name("Paste")
                    icon(MDI_CONTENT_PASTE, 14)
                }
                item {
                    shortcut(KeyCode.V, control = true, shift = true)
                    name("Paste Special")
                }
                separator()
                item {
                    name("Edit Cell")
                    icon(MDI_TABLE_EDIT, 14)
                    shortcut(KeyCode.BACK_QUOTE, control = true)
                }
                item {
                    shortcut(KeyCode.F, control = true)
                    name("Find and Replace")
                }
            }
        }

        Platform.runLater {
            columns.forEach {
                it.minWidth = 42.0
            }
        }
    }

    fun show() {
        base.layout.center = spreadsheet
        themeText.bind(Singleton.uiManager.themeProperty.asString())
        base.addStatus(selectionText, MDI_MOUSE)
        base.addStatus(zoomText, MDI_MAGNIFY_PLUS)
        base.addStatus(themeText, MDI_COMPARE)
        base.addStatus(Singleton.uiManager.memoryUsed, MDI_MEMORY)
        base.show()
    }

    private fun getRange(): String {
        val a = spreadsheet.selectionModel.selectedCells
        if (a.isEmpty()) {
            return "None"
        }
        val rows = a.map { it.row }
        val cols = a.map { it.column }

        val w = rows.min()!! + 1
        val x = rows.max()!! + 1
        val y = TableUtil.columnIndexToString(cols.min()!!)
        val z = TableUtil.columnIndexToString(cols.max()!!)

        return if (a.size == 1) {
            "$y$w"
        } else {
            "$y$w:$z$x"
        }
    }
}