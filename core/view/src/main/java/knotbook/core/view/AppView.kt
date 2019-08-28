package knotbook.core.view

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import knotbook.bowline.KnotTable
import knotbook.core.camera.KnotCameraTest
import knotbook.core.fx.*
import knotbook.core.registry.RegistryEditor
import knotbook.core.splash.AboutSplash
import knotbook.core.splash.GCSplash
import knotbook.pathplanner.runPathPlanner
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import kotlin.system.exitProcess

object AppView {

    private val bar = menuBar {
        isUseSystemMenuBar = true
        modify {
            menu {
                name("File")
                modify {
                    item {
                        name("Open Folder")
                        icon(FontAwesomeSolid.FOLDER_OPEN, 13)
                        shortcut(KeyCode.O, control = true)
                    }
                    item {
                        name("Close Folder")
                        shortcut(KeyCode.W, alt = true, shift = true)
                    }
                    item {
                        name("Folder Properties")
                    }
                    item {
                        name("Export as Archive")
                        shortcut(KeyCode.S, control = true, shift = true)
                    }
                    item {
                        name("Export as Workbook")
                        shortcut(KeyCode.S, control = true, alt = true)
                    }
                    separator()
                    item {
                        name("Create Table")
                        shortcut(KeyCode.N, control = true)
                    }
                    item {
                        name("Close Table")
                        shortcut(KeyCode.W, alt = true, control = true)
                    }
                    item {
                        name("Rename Table")
                        shortcut(KeyCode.DELETE, alt = true)
                    }
                    item {
                        name("Synchronize Data")
                        icon(FontAwesomeSolid.SYNC, 12)
                        shortcut(KeyCode.R, control = true)
                    }
                    item {
                        name("Reveal in Local")
                        shortcut(KeyCode.H, control = true)
                    }
                    item {
                        name("Reveal in Source")
                        shortcut(KeyCode.J, control = true)
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
                        name("Find")
                        icon(FontAwesomeSolid.SEARCH, 13)
                    }
                    item {
                        name("Replace")
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
                        name("Toggle Read-Only for Repository")
                        shortcut(KeyCode.L, control = true, shift = true)
                    }
                    item {
                        name("Toggle Read-Only for Table")
                        shortcut(KeyCode.L, control = true)
                    }
                }
            }
            menu {
                name("View")
                modify {
                    item {
                        name("Toggle Theme")
                        shortcut(KeyCode.F2)
                    }
                }
            }
            menu {
                name("Navigate")
                modify {
                    item { name("Toggle Sidebar") }
                    item { name("Expand Tree to Source") }
                    item { name("Collapse Tree") }
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
                        action { RegistryEditor.ish() }
                    }
                    item { name("Process Manager") }
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

    val mover = HBox().apply {
        prefWidth = 400.0
        prefHeight = 28.0
        minHeight = 24.0
        maxHeight = 24.0
        alignment = Pos.CENTER_LEFT
        background = Background(BackgroundFill(Color.valueOf("#eee"), null, null))
        add(bar)
    }

    val scene = Scene(vbox {
        val knotable = KnotTable()
        stylesheets.add("/knotbook.css")
        prefWidth = 800.0
        prefHeight = 600.0
        add(mover)
        add(hbox {
            add(vbox {
                background = Background(BackgroundFill(Color.WHITE, null, null))
                prefWidth = 240.0
                minWidth = 240.0
                alignment = Pos.TOP_CENTER
            })
            vgrow()
            add(knotable.hgrow())
        })
        knotable.requestFocus()
    })

    fun show(stage: Stage) {
        stage.title = "Knotbook"
        stage.icons.add(Image(AppView::class.java.getResourceAsStream("/icon.png")))
        stage.scene = AppView.scene
        stage.show()
    }

}