package kb.core.view

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
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


@Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode")
class DataView {

    private val base = WindowBase()

    private val mainMenus = fun Modifier<Menu>.() {
        menu {
            name("File")
            modify {
                item {
                    name("Open Recent")
                    shortcut(KeyCode.R, control = true)
                }
                separator()
                item {
                    name("Create Empty Table")
                    shortcut(KeyCode.N, control = true)
                    icon(MDI_PLUS, 14)
                    action {
                        DataView().also { dv ->
                            dv.base.stage.x = base.stage.x + 48.0
                            dv.base.stage.y = base.stage.y + 36.0
                            dv.show()
                        }
                    }
                }
                item {
                    name("Import Table from File")
                    icon(MDI_FILE_IMPORT, 14)
                    shortcut(KeyCode.O, control = true)
                    action {
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
                }
                item {
                    name("Rename Table")
                    icon(MDI_TEXTBOX, 14)
                    shortcut(KeyCode.F6, shift = true)
                }
                item {
                    name("Close Table")
                    shortcut(KeyCode.W, control = true)
                    action {
                        val alert = Alert(AlertType.CONFIRMATION, "Close Window?", ButtonType.YES, ButtonType.NO)
                        alert.showAndWait()
                        if (alert.result == ButtonType.YES) {
                            base.stage.close()
                        }
                    }
                }
                item {
                    name("Delete Table")
                    shortcut(KeyCode.DELETE, alt = true)
                    icon(MDI_DELETE_FOREVER, 14)
                }
                separator()
                item {
                    name("Exit")
                    action { Singleton.exitOK() }
                }
            }
        }
        menu {
            name("Edit")
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
                    name("Cut")
                    shortcut(KeyCode.X, control = true)
                    icon(MDI_CONTENT_CUT, 14)
                }

                item {
                    name("Copy")
                    icon(MDI_CONTENT_COPY, 14)
                    shortcut(KeyCode.C, control = true)
                }
                item {
                    name("Copy Special")
                    shortcut(KeyCode.C, control = true, shift = true)
                }
                item {
                    name("Paste")
                    shortcut(KeyCode.V, control = true)
                    icon(MDI_CONTENT_PASTE, 14)
                }
                item {
                    name("Paste Special")
                    shortcut(KeyCode.V, control = true, shift = true)
                }
                item {
                    name("Delete")
                    shortcut(KeyCode.DELETE)
                }
                separator()
                item {
                    name("Edit Cell")
                    icon(MDI_TABLE_EDIT, 14)
                    shortcut(KeyCode.BACK_QUOTE, control = true)
                }
                item {
                    name("Find and Replace")
                    shortcut(KeyCode.F, control = true)
                }
                separator()
                item {
                    name("Select All")
                    shortcut(KeyCode.A, control = true)
                    action { spreadsheet.selectionModel.selectAll() }
                }
                item {
                    name("Deselect All")
                    shortcut(KeyCode.A, control = true, shift = true)
                    action { spreadsheet.selectionModel.clearSelection() }
                }
            }
        }
        menu {
            name("View")
            modify {
                item {
                    name("Command Palette")
                    icon(MDI_CONSOLE, 14)
                    shortcut(KeyCode.K, control = true)
                    action { Singleton.uiManager.showCommandsBar() }
                }
                separator()
                item {
                    name("Toggle Colour Scheme")
                    shortcut(KeyCode.F3)
                    icon(MDI_COMPARE, 14)
                    action { Singleton.uiManager.toggleTheme() }
                }
                item {
                    name("Toggle Full Screen")
                    shortcut(KeyCode.F11)
                    action { base.toggleFullScreen() }
                }
                separator()
                item {
                    name("Zoom In")
                    icon(MDI_MAGNIFY_PLUS, 14)
                    action { spreadsheet.incrementZoom() }
                    shortcut(KeyCode.PLUS, control = true)
                }
                item {
                    name("Zoom Out")
                    icon(MDI_MAGNIFY_MINUS, 14)
                    action { spreadsheet.decrementZoom() }
                    shortcut(KeyCode.MINUS, control = true)
                }
                item {
                    name("Reset Zoom")
                    action { spreadsheet.zoomFactor = 1.0 }
                    shortcut(KeyCode.DIGIT0, control = true)
                }
            }
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
                item {
                    shortcut(KeyCode.DELETE)
                    name("Delete")
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
        base.scene.accelerators[KeyCodeCombination(KeyCode.COMMA, KeyCombination.CONTROL_DOWN)] = Runnable {
            Singleton.editAppProperties()
        }
        base.menuBar.modify(mainMenus)
        base.menuBar.modify(base.helpMenu)
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