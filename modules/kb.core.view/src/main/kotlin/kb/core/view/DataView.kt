package kb.core.view

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Menu
import javafx.scene.effect.DropShadow
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Popup
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.core.icon.icon
import kb.core.view.app.Singleton
import kb.core.view.app.WindowBase
import kb.service.api.array.TableArray
import kb.service.api.array.TableUtil
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetView
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.io.FileInputStream
import kotlin.concurrent.thread
import kotlin.system.exitProcess


@Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode")
class DataView {

    private val base = WindowBase()

    private val mainMenus = fun Modifier<Menu>.() {
        menu {
            name("File")
            modify {

                item {
                    name("Set Workspace")
                    icon(MDI_FOLDER_OUTLINE, 14)
                    shortcut(KeyCode.O, control = true, shift = true)
                    action {
                        val fc = FileChooser()
                        fc.title = "Open Workspace"
                        fc.showOpenDialog(base.stage)
                    }
                }
                item {
                    name("Open Recent")
                }
                item {
                    name("New Window")
                    shortcut(KeyCode.N, control = true)
                    action {
                        DataView().also { dv ->
                            dv.base.stage.x = base.stage.x + 48.0
                            dv.base.stage.y = base.stage.y + 36.0
                            dv.show()
                        }
                    }
                }
                item {
                    name("Close Window")
                    shortcut(KeyCode.W, control = true)
                    action {
                        base.stage.close()
                    }
                }
                separator()
                item {
                    name("New Empty Table")
                    shortcut(KeyCode.T, control = true)
                    icon(MDI_PLUS, 14)
                }
                item {
                    name("Import Table")
                    icon(MDI_FILE_IMPORT, 14)
                    shortcut(KeyCode.O, control = true)
                    action {
                        val fc = FileChooser()
                        fc.title = "Open Table from File"
                        val f = fc.showOpenDialog(base.stage)
                        if (f != null && f.extension == "csv") {
                            base.docLabel.text = "Loading"
                            thread {
                                try {
                                    val a = TableArray.fromCSV(FileInputStream(f), true)
                                    runOnFxThread {
                                        spreadsheet.grid = a.toGrid()
                                        spreadsheet.fixedRows.setAll(0)
                                        base.docLabel.text = f.absolutePath
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
                item {
                    name("Rename Table")
                    icon(MDI_TEXTBOX, 14)
                    shortcut(KeyCode.F6, shift = true)
                }
                item {
                    name("Delete Table")
                    shortcut(KeyCode.DELETE, alt = true)
                    icon(MDI_DELETE_FOREVER, 14)
                }
                separator()
                item {
                    name("Exit")
                    action { exitProcess(0) }
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
                    name("Option Bar")
                    icon(MDI_CONSOLE, 14)
                    shortcut(KeyCode.P, control = true, shift = true)
                    action { base.showOptionBarPrototype() }
                }
                item {
                    name("Navigate Workspace")
                    shortcut(KeyCode.TAB, control = true, shift = true)
                    action { hi() }
                }
                separator()
                item {
                    name("Toggle Colour Scheme")
                    shortcut(KeyCode.F3)
                    icon(MDI_COMPARE, 14)
                    action { base.toggleTheme() }
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

    private fun hi() {
        val a = Popup()
        a.content.add(vbox {

            padding = Insets(8.0)
            effect = DropShadow()
            style = "-fx-background-color: rgba(255,255,255, 0.95)"
            lb("Multi-Team Interface")
            lb("Match Schedule")
            lb("Raw Data")
            lb("Team Rankings")
            lb("Merged Data")
        })
        a.isAutoHide = true
        a.x = base.stage.x + 20
        a.y = base.stage.y + 40
        a.show(base.stage)
    }

    private fun VBox.lb(s: String) {
        add(hbox {
            align(Pos.CENTER_LEFT)
            add(fontIcon(MDI_CHEVRON_RIGHT, 18))
            spacing = 4.0
            add(label {
                text = s
//                style = "-fx-font-size: 18"
            })
        })
    }

    val zoomText = SimpleStringProperty("100%")
    val themeText = SimpleStringProperty("Light")
    val selectionText = SimpleStringProperty("None")

    private val spreadsheet = SpreadsheetView(GridBase(0, 0)).apply {
        selectionModel.selectedCells.addListener(InvalidationListener {
            selectionText.value = getRange()
        })
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


    private val mainView = splitPane {
        orientation = Orientation.HORIZONTAL
        vgrow()
        addFixed(spreadsheet)
        setDividerPositions(0.2, 0.6)
    }

    fun show() {
        base.scene.accelerators[KeyCodeCombination(KeyCode.K, KeyCombination.CONTROL_DOWN)] = Runnable {
            base.showOptionBarPrototype()
        }
        base.scene.accelerators[KeyCodeCombination(KeyCode.COMMA, KeyCombination.CONTROL_DOWN)] = Runnable {
            Singleton.editAppProperties()
        }
        base.menuBar.modify(mainMenus)
        base.menuBar.modify(base.helpMenu)
        base.layout.center = mainView
        themeText.bind(base.themeProperty.asString())
        base.addStatus(selectionText, MDI_MOUSE)
        base.addStatus(zoomText, MDI_MAGNIFY_PLUS)
        base.addStatus(themeText, MDI_COMPARE)
        base.addStatus(Singleton.memoryUsed, MDI_MEMORY)
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
            "$y$w:$z$x [${a.size}]"
        }
    }
}