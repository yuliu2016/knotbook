package kb.core.view

import javafx.beans.InvalidationListener
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.Separator
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.Popup
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.core.icon.icon
import kb.core.view.app.Singleton
import kb.core.view.splash.AboutSplash
import kb.core.view.splash.GCSplash
import kb.service.api.util.TableHeaders
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetView
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.util.*
import kotlin.system.exitProcess


class DataView {

    private val barCreator: Modifier<Menu>.() -> Unit = {
        menu {
            name("File")
            modify {
                item {
                    name("Open Folder")
                    icon(MDI_FOLDER_OUTLINE, 14)
                    shortcut(KeyCode.O, control = true)
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
                        fc.showOpenDialog(stage)
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
                    name("Restart")
                }
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
                }
                separator()
                item {
                    name("Toggle Colour Scheme")
                    shortcut(KeyCode.F3)
                    icon(MDI_COMPARE, 14)
                    action { toggleTheme() }
                }
                item {
                    name("Toggle Tree View")
                    shortcut(KeyCode.F4)
                }
                item {
                    name("Toggle Full Screen")
                    shortcut(KeyCode.F11)
                    action { toggleFullScreen() }
                }
                separator()
                item {
                    name("Expand Tree")
                    icon(MDI_UNFOLD_MORE, 14)
                }
                item {
                    name("Collapse Tree")
                    icon(MDI_UNFOLD_LESS, 14)
                }
                separator()
                item {
                    name("Open in New Window")
                    shortcut(KeyCode.N, control = true)
                    action {
                        DataView().show()
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
        menu {
            name("Help")
            modify {
                item {
                    name("Mark for Garbage Collection")
                    action { GCSplash.splash() }
                    icon(MDI_DELETE_SWEEP, 14)
                    shortcut(KeyCode.B, control = true)
                }
                item {
                    name("JVM Properties")
                    action {
                        val properties = System
                                .getProperties()
                                .entries
                                .sortedBy { it.key.toString() }
                                .joinToString("\n") {
                                    val strVal = it.value.toString()
                                    val value = when {
                                        strVal.endsWith("\\") -> "'$strVal'"
                                        strVal == System.lineSeparator() -> "LINE_SEPARATOR"
                                        else -> strVal
                                    }
                                    "${it.key}=$value"
                                }
                        Singleton.context.createTextEditor().apply {
                            title = "JVM Properties (Read-Only)"
                            syntax = "text/properties"
                            setInitialText(properties)
                            show()
                        }
                    }
                }
                item {
                    name("Plugins and Services")
                    action {
                        val t = Singleton.context.services.joinToString("\n") {
                            it.metadata.run { "$packageName => $packageVersion" }
                        }
                        Singleton.context.createTextEditor().apply {
                            title = "Plugins and Services"
                            setInitialText(t)
                            show()
                        }
                    }
                }
                separator()
                item {
                    name("About")
                    action { AboutSplash.splash(stage) }
                    icon(MDI_INFORMATION_OUTLINE, 14)
                    shortcut(KeyCode.F1)
                }
                item {
                    name("Open Source Licenses")
                }

            }
        }
    }

    private var isFullScreen = false

    private fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        stage.isFullScreen = isFullScreen
    }

    private var theme = Theme.Light

    private fun toggleTheme() {
        theme = when (theme) {
            Theme.Light -> Theme.Dark
            Theme.Dark -> Theme.Light
        }
        box.stylesheets.setAll("/knotbook.css", theme.fileName)
        components.themeLabel.text = theme.name
    }

    private val stage = Stage()
    private val indexTree = IndexTree()

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

    private val box = vbox {
        stylesheets.addAll("/knotbook.css", Theme.Light.fileName)
        prefWidth = 1120.0
        prefHeight = 630.0
        val bar = menuBar { modify(barCreator) }
        add(bar)
        bar.isUseSystemMenuBar = true
        add(splitPane {
            orientation = Orientation.HORIZONTAL
            vgrow()
            addFixed(indexTree.tree, spreadsheet)
            setDividerPositions(0.2, 0.6)
        })
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

    private val scene = Scene(box)

    private fun shift() {
        val popup = Popup()
        popup.content.add(vbox {
            stylesheets.addAll("/knotbook.css", Theme.Light.fileName)
            style = "-fx-background-color: white"
            effect = DropShadow().apply {
                color = Color.GRAY
                height = 10.0
                width = 10.0
                radius = 10.0
            }
            prefWidth = 600.0
            prefHeight = 480.0
            add(vbox {
                align(Pos.TOP_CENTER)
//                add(Label("Enter a Command or Formula "))
                padding = Insets(8.0)
                spacing = 4.0
                add(textField {
                    styleClass("formula-field")
                })
            })

            add(listView<Entity> {
                vgrow()
                items = getList().observable()
                setCellFactory {
                    EntityListCell()
                }
            })

        })
        popup.isAutoHide = true
        popup.show(stage)
        popup.centerOnScreen()
    }

    fun show() {
        scene.accelerators[KeyCodeCombination(KeyCode.BACK_QUOTE, KeyCombination.CONTROL_DOWN)] = Runnable {
            shift()
        }
        scene.accelerators[KeyCodeCombination(KeyCode.COMMA, KeyCombination.CONTROL_DOWN)] = Runnable {
            Singleton.context.createTextEditor().apply {
                title = "Application Properties"
                setEditable(true)
                addAction("Save Changes") {
                    if (isTextChanged) {
                        Singleton.context.props.setInputText(finalText)
                    }
                }
                syntax = "text/properties"
                setInitialText(Singleton.context.props.joinedText)
                show()
            }
        }

        stage.fullScreenExitHint = "Press F11 to Exit Full Screen"
        stage.title = "KnotBook"
        stage.icons.add(Image(DataView::class.java.getResourceAsStream("/icon.png")))
        stage.scene = scene
        stage.show()
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