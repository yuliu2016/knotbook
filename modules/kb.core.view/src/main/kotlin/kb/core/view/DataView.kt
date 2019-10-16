package kb.core.view

import javafx.beans.InvalidationListener
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.Separator
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.FileChooser
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.core.icon.icon
import kb.core.view.app.Singleton
import kb.service.api.util.TableHeaders
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetView
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.util.*
import kotlin.system.exitProcess


class DataView {

    private val base = WindowBase()

    private val mainMenus = fun Modifier<Menu>.() {
        menu {
            name("File")
            modify {
                item {
                    name("Open Folder")
                    icon(MDI_FOLDER_OUTLINE, 14)
                    shortcut(KeyCode.O, control = true)
                    action {
                        val fc = FileChooser()
                        fc.title = "Open Folder"
                        fc.showOpenDialog(base.stage)
                    }
                }
                item {
                    name("Open Recent")
                }
                item {
                    name("Close Folder")
                }
                separator()
                item {
                    name("Open Table from File")
                    action {
                        val fc = FileChooser()
                        fc.title = "Open Table from File"
                        fc.showOpenDialog(base.stage)
                    }
                }
                item {
                    name("Create Empty Table")
                    shortcut(KeyCode.T, control = true)
                    icon(MDI_PLUS, 14)
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
                }
                separator()
                item {
                    name("Find and Replace")
                    shortcut(KeyCode.F, control = true)
                }
                separator()
                item {
                    name("Select All")
                    shortcut(KeyCode.A, control = true)
                }
                item {
                    name("Deselect All")
                    shortcut(KeyCode.A, control = true, shift = true)
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
                separator()
                item {
                    name("Toggle Colour Scheme")
                    shortcut(KeyCode.F3)
                    icon(MDI_COMPARE, 14)
                    action { base.toggleTheme() }
                }
                item {
                    name("Toggle Tree View")
                    shortcut(KeyCode.F4)
                    action {
                        if (sp.items.size == 1) {
                            sp.items.add(0, indexTree.tree)
                            sp.setDividerPositions(splitDivider)
                        } else {
                            splitDivider = sp.dividerPositions[0]
                            sp.items.remove(indexTree.tree)
                        }
                    }
                }
                item {
                    name("Toggle Full Screen")
                    shortcut(KeyCode.F11)
                    action { base.toggleFullScreen() }
                }
                separator()
                item {
                    name("Expand Tree")
                    icon(MDI_UNFOLD_MORE, 14)
                    action { indexTree.tree.root.children.forEach { it.isExpanded = true } }

                }
                item {
                    name("Collapse Tree")
                    icon(MDI_UNFOLD_LESS, 14)
                    action { indexTree.tree.root.children.forEach { it.isExpanded = false } }
                }
                separator()
                item {
                    name("Open in New Window")
                    shortcut(KeyCode.N, control = true)
                    action {
                        DataView().also { dv ->
                            dv.base.stage.x = base.stage.x + 48.0
                            dv.base.stage.y = base.stage.y + 36.0
                            dv.splitDivider = dv.sp.dividerPositions[0]
                            dv.sp.items.remove(dv.indexTree.tree)
                            dv.show()
                        }
                    }
                }
                item {
                    name("Close Current Table")
                    shortcut(KeyCode.W, control = true, shift = true)
                }
                separator()
                item {
                    name("Zoom In")
                    icon(MDI_MAGNIFY_PLUS, 14)
                    shortcut(KeyCode.PLUS, control = true)
                }
                item {
                    name("Zoom Out")
                    icon(MDI_MAGNIFY_MINUS, 14)
                    shortcut(KeyCode.MINUS, control = true)
                }
                item {
                    name("Reset Zoom")
                    shortcut(KeyCode.DIGIT0, control = true)
                }
            }
        }
    }

    private var splitDivider = 0.2

    private val indexTree = FolderTree()
    private val components = AppComponents()

    private val spreadsheet = SpreadsheetView().apply {
        selectionModel.selectedCells.addListener(InvalidationListener {
            components.selectionLabel.text = getRange()
        })
        zoomFactorProperty().addListener { _, _, nv ->
            components.zoomLabel.text = "${(nv.toDouble() * 100).toInt()}%"
        }
        hgrow()
        contextMenu = contextMenu {
            modify {
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
                    name("Delete")
                }
                separator()
                item {
                    shortcut(KeyCode.F, control = true)
                    name("Find and Replace")
                }
                separator()
                item {
                    shortcut(KeyCode.PLUS, control = true)
                    name("Zoom In")
                    icon(MDI_MAGNIFY_PLUS, 14)
                }
                item {
                    shortcut(KeyCode.MINUS, control = true)
                    name("Zoom Out")
                    icon(MDI_MAGNIFY_MINUS, 14)
                }
                item {
                    shortcut(KeyCode.DIGIT0, control = true)
                    name("Reset Zoom")
                }
            }
        }
        val a = grid as GridBase
        a.setRowHeightCallback { 20.0 }
        a.setResizableRows(BitSet())
        columns.forEach {
            it.setPrefWidth(84.0)
            it.minWidth = 42.0
        }
        grid.rows.first().forEach {
            it.style = "-fx-font-weight:bold; -fx-alignment: CENTER"
        }
        fixedRows.add(0)
    }

    private val sp = splitPane {
        orientation = Orientation.HORIZONTAL
        vgrow()
        addFixed(indexTree.tree, spreadsheet)
        setDividerPositions(0.2, 0.6)
    }

    private val box = vbox {
        add(sp)
        add(hbox {
            align(Pos.CENTER_LEFT)
            padding = Insets(0.0, 8.0, 0.0, 8.0)
            prefHeight = 20.0
            styleClass("status-bar")
            spacing = 8.0
            add(fontIcon(MDI_FOLDER_MULTIPLE_OUTLINE, 14))
            add(Separator(Orientation.VERTICAL))
            add(Label("Ready").apply {
                graphic = fontIcon(MDI_INFORMATION, 14)
            })
            hspace()

            add(Separator(Orientation.VERTICAL))
            add(components.calcLabel)
            add(Separator(Orientation.VERTICAL))
            add(components.selectionLabel)
            add(Separator(Orientation.VERTICAL))
            add(components.zoomLabel)
            add(Separator(Orientation.VERTICAL))
            add(components.themeLabel)
            add(Separator(Orientation.VERTICAL))
            add(components.heapLabel)

        })
        isSnapToPixel = false
    }


    fun show() {
        base.scene.accelerators[KeyCodeCombination(KeyCode.BACK_QUOTE, KeyCombination.CONTROL_DOWN)] = Runnable {
            base.showOptionBarPrototype()
        }
        base.scene.accelerators[KeyCodeCombination(KeyCode.COMMA, KeyCombination.CONTROL_DOWN)] = Runnable {
            Singleton.editAppProperties()
        }
        base.menuBar.modify(mainMenus)
        base.menuBar.modify(base.helpMenu)
        base.layout.center = box
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
        val y = TableHeaders.columnIndexToString(cols.min()!!)
        val z = TableHeaders.columnIndexToString(cols.max()!!)

        return if (a.size == 1) {
            "$y$w"
        } else {
            "$y$w:$z$x [${a.size}]"
        }
    }
}