package kb.core.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.layout.Region
import javafx.stage.Stage
import kb.core.bowline.testBowline
import kb.core.camera.fx.KnotCameraTest
import kb.core.code.CodeEditor
import kb.core.code.Syntax
import kb.core.context.Registry
import kb.core.fx.*
import kb.core.splash.AboutSplash
import kb.core.splash.GCSplash
import kb.path.planner.runPathPlanner
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import kotlin.system.exitProcess

object AppView {

    private val barCreator: Modifier<Menu>.() -> Unit = {
        menu {
            name("File")
            modify {
                item {
                    name("Create/Open Folder")
                    icon(MDI_FOLDER_OUTLINE, 14)
                    shortcut(KeyCode.O, control = true)
                }
                item {
                    name("Close Folder")
                }
                separator()
                item {
                    name("Commit")
                    icon(MDI_CHECK, 14)
                    shortcut(KeyCode.K, control = true)
                }
                item {
                    name("Pull")
                    icon(MDI_SOURCE_PULL, 14)
                    shortcut(KeyCode.T, control = true)
                }
                item {
                    name("Push")
                    icon(MDI_CLOUD_UPLOAD, 14)
                    shortcut(KeyCode.K, control = true, shift = true)
                }
                item {
                    name("Show History")
                    icon(MDI_HISTORY, 14)
                }
                separator()
                item {
                    name("Print")
                    icon(MDI_PRINTER, 14)
                }
                item {
                    name("Export as Zip Archive")
                }
                item {
                    name("Export as Excel Workbook")
                }
                separator()
                item {
                    name("Synchronize")
                    icon(MDI_RELOAD, 14)
                    shortcut(KeyCode.Y, control = true, alt = true)
                }
                item {
                    name("Application Properties")
                    icon(MDI_TUNE, 14)
                    shortcut(KeyCode.P, control = true)
                    action {
                        CodeEditor("Application Properties", true,
                                "Save", "Discard", Registry.join(), { s ->
                            Registry.parse(s.split("\n"))
                            Registry.save()
                        }, Syntax.Properties)
                    }
                }
                separator()
                item {
                    name("Open Terminal in Folder")
                    icon(MDI_CONSOLE, 14)
                    shortcut(KeyCode.F12, alt = true)
                }
                item {
                    name("Reveal in Local Cache")
                }
                item {
                    name("Reveal in Data Source")
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
                separator()
                item {
                    name("Revert Changes")
                    shortcut(KeyCode.Z, control = true, alt = true)
                }
                separator()
                item {
                    name("Edit CSV as Text")
                    icon(MDI_FILE_DELIMITED, 14)
                    action {
                        CodeEditor("Edit CSV", true, "Save", "Discard",
                                "A,B,C\n1,2,4", {}, Syntax.CSV)
                    }
                    shortcut(KeyCode.E, alt = true)
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
                    name("Split Table Vertically")
                    icon(MDI_SWAP_HORIZONTAL, 14)
                }
                item {
                    name("Split in New Window")
                    shortcut(KeyCode.N, control = true, shift = true)
                }
                item {
                    name("Close Current Table")
                    shortcut(KeyCode.W, control = true)
                }
                item {
                    name("Close Others")
                    shortcut(KeyCode.W, control = true, alt = true)
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
                separator()
                item {
                    name("Freeze Columns")
                }
                item {
                    name("Unfreeze Columns")
                }
            }
        }
        menu {
            name("Data")
            modify {
                item {
                    name("Create Empty Table")
                    shortcut(KeyCode.N, control = true)
                    icon(MDI_PLUS, 14)
                }
                item {
                    name("Duplicate Table")
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
                separator()
                item {
                    name("Rebuild Toolbar")
                    shortcut(KeyCode.R, control = true)
                    icon(MDI_WRENCH, 14)
                }
                item {
                    name("Find and Replace")
                    icon(MDI_FILE_FIND, 14)
                    shortcut(KeyCode.F, control = true)
                }
                item {
                    name("Cell and Formula Editor")
                }
                item {
                    name("Sort and Filter Toolbar")
                }
                item {
                    name("Format Toolbar")
                    shortcut(KeyCode.DIGIT1, control = true)
                    icon(MDI_FORMAT_PAINT, 14)
                }
            }
        }
        menu {
            name("Start")
            modify {
                item {
                    name("WebCam View")
                    icon(MDI_CAMERA, 14)
                    action { KnotCameraTest.test() }
                }
                item {
                    name("Drive Path Planner")
                    icon(MDI_NAVIGATION, 13)
                    action { runPathPlanner() }
                }
                item {
                    name("Scenic View")
                    action { Alert(Alert.AlertType.INFORMATION, "Scenic View is not supported in this build").show() }
                    icon(MDI_CLOUD_OUTLINE, 14)
                }
                item {
                    name("Bowline Table")
                    icon(MDI_BOWL, 14)
                    action { testBowline() }
                }
                separator()
                item {
                    name("Garbage Collection Cycle")
                    action { GCSplash.splash() }
                    shortcut(KeyCode.B, control = true)
                }
            }
        }
        menu {
            name("Help")
            modify {
                item {
                    name("Show JVM Properties")
                    icon(MDI_COFFEE, 14)
                    action {
                        val properties = System.getProperties().entries.sortedBy { it.key.toString() }
                                .joinToString("\n") { "${it.key}=${it.value}" }
                        CodeEditor("JVM Properties", false, "Ok", "Close",
                                properties, {}, Syntax.Properties)
                    }
                }
                item {
                    name("Show releases on GitHub")
                    icon(MDI_GITHUB_CIRCLE, 14)
                }
                separator()
                item {
                    name("About")
                    action { AboutSplash.splash() }
                    icon(MDI_INFORMATION_OUTLINE, 14)
                    shortcut(KeyCode.F1)
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
    }

    private val stage = Stage()
    private val indexTree = IndexTree()

    private val box = vbox {
        val table = TableView<Int>()
        table.items = (0..100).toList().observable()
        stylesheets.addAll("/knotbook.css", Theme.Light.fileName)
        prefWidth = 1120.0
        prefHeight = 630.0
        val bar = menuBar { modify(barCreator) }
        add(bar)
        bar.isUseSystemMenuBar = true
        add(splitPane {
            orientation = Orientation.HORIZONTAL
            vgrow()
            addFixed(indexTree.tree, vbox {
                add(textField {
                    promptText = "Enter formula here"
                })
                add(table.vgrow())
            })
            setDividerPositions(0.2)
        })
        table.columns.addAll((0..10).map { col ->
            TableColumn<Int, String>(col.toString()).apply {
                this.setCellValueFactory {
                    SimpleStringProperty((col * it.value).toString())
                }
                this.prefWidth = 100.0
                isSortable = false
            }
        })
        table.fixedCellSize = Region.USE_COMPUTED_SIZE
        table.selectionModel.isCellSelectionEnabled = true
        table.selectionModel.selectionMode = SelectionMode.MULTIPLE
        table.requestFocus()
    }

    private val scene = Scene(box)

    fun show() {
        stage.fullScreenExitHint = "Press F11 to Exit Full Screen"
        stage.title = "KnotBook"
        stage.icons.add(Image(AppView::class.java.getResourceAsStream("/icon.png")))
        stage.scene = scene
        stage.show()
    }
}