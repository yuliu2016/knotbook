package kb.core.view

import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import kb.core.bowline.Bowline
import kb.core.camera.fx.KnotCameraTest
import kb.core.code.CodeEditor
import kb.core.code.Syntax
import kb.core.context.Registry
import kb.core.fx.*
import kb.core.splash.AboutSplash
import kb.core.splash.GCSplash
import kb.path.planner.runPathPlanner
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.materialdesign.MaterialDesign
import kotlin.system.exitProcess

object AppView {

    private val bar = menuBar {
        isUseSystemMenuBar = true
        modify {
            menu {
                name("File")
                modify {
                    item {
                        name("Create/Open Folder")
                        icon(MaterialDesign.MDI_FOLDER_OUTLINE, 14)
                        shortcut(KeyCode.O, control = true)
                    }
                    item {
                        name("Create Table")
                        shortcut(KeyCode.N, control = true)
                    }
                    separator()
                    item {
                        name("Commit")
                        icon(MaterialDesign.MDI_CHECK, 14)
                        shortcut(KeyCode.K, control = true)
                    }
                    item {
                        name("Pull")
                        icon(MaterialDesign.MDI_SOURCE_PULL, 14)
                        shortcut(KeyCode.T, control = true)
                    }
                    item {
                        name("Push")
                        icon(MaterialDesign.MDI_CLOUD_UPLOAD, 14)
                        shortcut(KeyCode.K, control = true, shift = true)
                    }
                    item {
                        name("Show History")
                        icon(MaterialDesign.MDI_HISTORY, 14)
                    }
                    item {
                        name("Revert Changes")
                        shortcut(KeyCode.Z, control = true, alt = true)
                    }
                    separator()
                    item {
                        name("Print")
                        icon(MaterialDesign.MDI_PRINTER, 14)
                    }
                    item {
                        name("Export as Archive")
                    }
                    item {
                        name("Export as Excel Workbook")
                    }
                    separator()
                    item {
                        name("Synchronize")
                        icon(MaterialDesign.MDI_RELOAD, 14)
                        shortcut(KeyCode.Y, control = true, alt = true)
                    }
                    separator()
                    item {
                        name("Open Terminal in Folder")
                        icon(MaterialDesign.MDI_CONSOLE, 14)
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
                        name("Rename Table")
                        icon(MaterialDesign.MDI_TEXTBOX, 14)
                        shortcut(KeyCode.F6, shift = true)
                    }
                    item {
                        name("Find")
                        icon(FontAwesomeSolid.SEARCH, 13)
                        shortcut(KeyCode.F, control = true)
                    }
                    item {
                        name("Replace")
                        shortcut(KeyCode.R, control = true)
                    }
                    item {
                        name("Copy")
                        icon(FontAwesomeSolid.COPY, 13)
                        shortcut(KeyCode.C, control = true)
                    }
                    item {
                        name("Copy Special")
                        shortcut(KeyCode.C, control = true, shift = true)
                    }
                    item {
                        name("Lock Table")
                        shortcut(KeyCode.L, control = true)
                    }
                }
            }
            menu {
                name("View")
                modify {
                    item {
                        name("Toggle Theme")
                        shortcut(KeyCode.F3)
                        icon(FontAwesomeSolid.ADJUST, 13)
                        action { toggleTheme() }
                    }
                    item {
                        name("Toggle Full Screen")
                        shortcut(KeyCode.F11)
                        action { toggleFullScreen() }
                    }
                    item {
                        name("Toggle Table Features")
                    }
                    separator()
                    item {
                        name("Zoom In")
                        icon(FontAwesomeSolid.SEARCH_PLUS, 13)
                    }
                    item {
                        name("Zoom Out")
                        icon(FontAwesomeSolid.SEARCH_MINUS, 13)
                    }
                    item {
                        name("Reset Zoom")
                    }
                    separator()
                    item {
                        name("Open Table in New Window")
                    }
                    item {
                        name("Hide Table")
                        shortcut(KeyCode.W, control = true)
                    }
                }
            }
            menu {
                name("Tree")
                modify {
                    item {
                        name("Toggle Visibility")
                        icon(FontAwesomeSolid.EYE, 13)
                    }
                    item {
                        name("Toggle Logs")
                        icon(FontAwesomeSolid.HISTORY, 13)
                    }
                    item {
                        name("Toggle Metric View")
                        icon(FontAwesomeSolid.TAPE, 13)
                    }
                    separator()
                    item {
                        name("Collapse All")
                    }
                    item {
                        name("Expand All")
                    }
                    item {
                        name("Expand to Current Table")
                    }
                    item {
                        name("Collapse Tree")
                    }
                    separator()
                    item {
                        name("Move Feature Up")
                    }
                    item {
                        name("Move Feature Down")
                    }
                    item {
                        name("Delete Feature")
                    }
                    item {
                        name("Supress Feature")
                    }
                    separator()
                    item {
                        name("Feature Properties")
                        shortcut(KeyCode.SPACE)
                    }
                }
            }
            menu {
                name("Start")
                modify {
                    item {
                        name("Data Agent Manager")
                        icon(FontAwesomeSolid.FILE_IMPORT, 13)
                    }
                    item {
                        name("WebCam View")
                        icon(FontAwesomeSolid.CAMERA_RETRO, 13)
                        action {
                            KnotCameraTest.test()
                        }
                    }
                    item {
                        name("Plugin Manager")
                        icon(FontAwesomeSolid.CUBE, 13)
                    }
                    item {
                        name("Drive Path Planner")
                        icon(FontAwesomeSolid.LOCATION_ARROW, 13)
                        action { runPathPlanner() }
                    }
                    item {
                        name("Scenic View")
                        action { Alert(Alert.AlertType.INFORMATION, "Scenic View is not supported in this build").show() }
                        icon(FontAwesomeSolid.IMAGE, 13)
                    }
                    item {
                        name("Application Properties")
                        icon(FontAwesomeSolid.TOOLS, 13)
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
                        name("Show releases on GitHub")
                    }
                    item {
                        name("About")
                        action { AboutSplash.splash() }
                        shortcut(KeyCode.F1)
                    }
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

    val stage = Stage()

    private val box = vbox {
        val bowline = Bowline()
        stylesheets.addAll("/knotbook.css", Theme.Light.fileName)
        prefWidth = 1120.0
        prefHeight = 630.0
        add(bar)
        add(hbox {
            add(DashboardActivity().view.indexTree)
            vgrow()
            add(bowline.hgrow())
        })
        bowline.requestFocus()
    }

    private val scene = Scene(box)

    fun show() {
        stage.fullScreenExitHint = "Press F11 to Exit"
        stage.title = "KnotBook"
        stage.icons.add(Image(AppView::class.java.getResourceAsStream("/icon.png")))
        stage.scene = scene
        stage.show()
    }
}