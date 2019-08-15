package knotbook.application

import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import knotbook.core.camera.KnotCameraTest
import knotbook.core.fx.*
import knotbook.core.registry.Registry
import knotbook.core.registry.RegistryEditor
import knotbook.core.splash.GCSplash
import knotbook.core.splash.Splash
import knotbook.core.table.Knotable
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import kotlin.system.exitProcess


class Main : Application() {

    private val bar = menuBar {
        isUseSystemMenuBar = true
        modify {
            menu {
                name("File")
                modify {
                    item {
                        name("New Table")
                        shortcut(KeyCode.N, control = true)
                    }
                    item {
                        name("Delete Table")
                        shortcut(KeyCode.DELETE, alt = true)
                    }
                    item {
                        name("Synchronize")
                        icon(FontAwesomeSolid.SYNC, 13)
                        shortcut(KeyCode.R, control = true)
                    }
                    separator()
                    item {
                        name("New Repository")
                        shortcut(KeyCode.N, control = true, alt = true)
                    }
                    item {
                        name("Open Repository")
                        icon(FontAwesomeSolid.FOLDER_OPEN, 13)
                        shortcut(KeyCode.O, control = true)
                    }
                    item {
                        name("Reveal Context in Source")
                        shortcut(KeyCode.J, control = true)
                    }
                    separator()
                    item {
                        name("Toggle Theme")
                        shortcut(KeyCode.F2)
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
                name("Navigate")
                modify {
                    item { name("Toggle Sidebar") }
                    item { name("Expand Tree to Source") }
                    item { name("Collapse Tree") }
                }
            }
            menu {
                name("Help")
                modify {
                    item { name("Process Manager") }
                    item {
                        name("Test Camera")
                        action {
                            KnotCameraTest.test()
                        }
                    }
                    item { name("Plugin Manager") }
                    item { name("Show Log File") }
                    item {
                        name("Application Registry")
                        icon(FontAwesomeSolid.ADDRESS_BOOK, 13)
                        action { RegistryEditor.show() }
                    }
                    item {
                        name("Start GC Cycle")
                        action { GCSplash.splash() }
                        shortcut(KeyCode.B, control = true)
                    }
                    separator()
                    item {
                        name("About")
                        action { Splash.splash() }
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

        add(hbox {
            alignment = Pos.CENTER
            prefWidth = 8.0
            children.add(ImageView(Image(Main::class.java.getResourceAsStream("/knotbook/application/icon.png"))).apply {
                isPreserveRatio = true
                fitHeight = 18.0
                image = null
            })
        })
        add(bar)
    }

    val scene = Scene(vbox {
        val knotable = Knotable()
        stylesheets.add("/knotbook.css")
        prefWidth = 800.0
        prefHeight = 600.0
        add(mover)
        add(hbox {
            add(vbox {
                background = Background(BackgroundFill(Color.WHITE, null, null))
                prefWidth = 300.0
                minWidth = 300.0
                alignment = Pos.TOP_CENTER
            })
            add(knotable)
        })
        knotable.requestFocus()
    })

    override fun start(stage: Stage) {
        stage.title = "Knotbook"
        stage.icons.add(Image(Main::class.java.getResourceAsStream("/knotbook/application/icon.png")))
        stage.scene = scene
        stage.initStyle(StageStyle.DECORATED)
        stage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Registry.load()
            launch(Main::class.java)
        }
    }
}