package kb.core.view

import javafx.application.Platform
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
import javafx.stage.Popup
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.icon
import kb.core.splash.AboutSplash
import kb.core.splash.GCSplash
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess


class AppView {

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
                    name("Close Folder")
                }
                separator()
                item {
                    name("Open Table from File")
                }
                item {
                    name("Create Empty Table")
                    shortcut(KeyCode.N, control = true)
                    icon(MDI_PLUS, 14)
                }
                item {
                    name("Rename Table")
                    icon(MDI_TEXTBOX, 14)
                    shortcut(KeyCode.F6, shift = true)
                }
                item {
                    name("Mark Table Read-Only")
                    shortcut(KeyCode.L, control = true)
                    icon(MDI_LOCK, 14)
                }
                item {
                    name("Delete Table")
                    shortcut(KeyCode.DELETE, alt = true)
                    icon(MDI_DELETE_FOREVER, 14)
                }
/*                separator()
                item {
                    name("Application Properties")
                    icon(MDI_TUNE, 14)
                    shortcut(KeyCode.P, control = true)
//                    action {
//                        CodeEditor("Application Properties", true,
//                                "Save", "Discard", Registry.join(), { s ->
//                            Registry.parse(s.split("\n"))
//                            Registry.save()
//                        }, Syntax.Properties)
//                    }
                }*/
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
                    name("Copy Tab-Delimited")
                    icon(MDI_CONTENT_COPY, 14)
                    shortcut(KeyCode.C, control = true)
                }
                item {
                    name("Copy Comma-Delimited")
                }
                item {
                    name("Copy Dictionary")
                    shortcut(KeyCode.C, control = true, shift = true)
                }
                item {
                    name("Paste")
                    shortcut(KeyCode.V, control = true)
                    icon(MDI_CONTENT_PASTE, 14)
                }
                item {
                    name("Paste From History")
                    shortcut(KeyCode.V, control = true, shift = true)
                }
                item {
                    name("Delete")
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
                    name("Toggle Colour Scheme")
                    shortcut(KeyCode.F3)
                    icon(MDI_COMPARE, 14)
                    action { toggleTheme() }
                }
                item {
                    name("Enter Full Screen")
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
                    shortcut(KeyCode.N, control = true, shift = true)
                }
                item {
                    name("Close Current Table")
                    shortcut(KeyCode.W, control = true)
                }
                separator()
                item {
                    name("Zoom In")
                    icon(MDI_MAGNIFY_PLUS, 14)
                    shortcut(KeyCode.PLUS, control = true)
                }
                item {
                    name("Zoom Out")
                    icon(MDI_MAGNIFY_PLUS, 14)
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
                //                item {
//                    name("Try BowlineTableView")
//                    icon(MDI_BOWL, 14)
//                    action {
//                        val stage = Stage()
//                        stage.title = "BowlineTableView"
//                        stage.scene = Scene(BowlineTable())
//                        stage.scene.stylesheets.addAll("/bowline.css")
//                        stage.width = 900.0
//                        stage.height = 600.0
//                        stage.show()
//                    }
//                }
//                item {
//                    name("Debug with Scenic View")
//                    action { alertDialog("Notice", "Scenic View is not supported in this build") }
//                    icon(MDI_CLOUD_OUTLINE, 14)
//                }
//                separator()
                item {
                    name("Mark for Garbage Collection")
                    action { GCSplash.splash() }
                    icon(MDI_DELETE_SWEEP, 14)
                    shortcut(KeyCode.B, control = true)
                }
                /*item {
                    name("Revert App Properties to Default")
                }
                item {
                    name("JVM Properties")
//                    action {
//                        val properties = System
//                                .getProperties()
//                                .entries
//                                .sortedBy { it.key.toString() }
//                                .joinToString("\n") {
//                                    val strVal = it.value.toString()
//                                    val value = when {
//                                        strVal.endsWith("\\") -> "'$strVal'"
//                                        strVal == System.lineSeparator() -> "LINE_SEPARATOR"
//                                        else -> strVal
//                                    }
//                                    "${it.key}=$value"
//                                }
//                        CodeEditor("JVM Properties (Read-Only)",
//                                false, "Ok", "Close",
//                                properties, {}, Syntax.Properties)
//                    }
                }*/
                separator()
                item {
                    name("About")
                    action { AboutSplash.splash() }
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
    private val table1 = TableContainer()

    private val components = AppComponents()

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
            addFixed(indexTree.tree, table1.view)
            setDividerPositions(0.2, 0.6)
        })
        add(hbox {
            align(Pos.CENTER_LEFT)
            padding = Insets(0.0, 8.0, 0.0, 8.0)
            prefHeight = 20.0
            styleClass("status-bar")
            spacing = 8.0
            add(Label("Ready"))
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

    private var lastShift = 0L

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
                add(Label("Enter a Command or Formula "))
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

        thread(isDaemon = true, name = "MemoryObserver") {
            while (true) {
                val m  = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1E6).toInt() + 1
                Platform.runLater {
                    components.heapLabel.text = "${m}M"
                }
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }

        stage.fullScreenExitHint = "Press F11 to Exit Full Screen"
        stage.title = "KnotBook"
        stage.icons.add(Image(AppView::class.java.getResourceAsStream("/icon.png")))
        stage.scene = scene
        stage.show()
    }
}