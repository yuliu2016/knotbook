package knotbook.application

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import knotbook.core.snap.SnapScene
import knotbook.core.splash.Splash
//import knotbook.core.splash.Splash
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign


class Main : Application() {
    override fun start(stage: Stage) {
        stage.title = "Knotable"
        stage.icons.add(Image(Main::class.java.getResourceAsStream("/knotbook/application/knot-tb.png")))
        val knotable = Knotable()
        val min = Button("", FontIcon.of(MaterialDesign.MDI_WINDOW_MINIMIZE, 17)).apply {
            styleClass.add("minmax-button")
        }
        val max = Button("", FontIcon.of(MaterialDesign.MDI_WINDOW_MAXIMIZE, 17)).apply {
            styleClass.add("minmax-button")
        }
        val mover = HBox().apply {
            prefWidth = 400.0
            prefHeight = 28.0
            minHeight = 28.0
            maxHeight = 28.0
            alignment = Pos.CENTER_LEFT
            background = Background(BackgroundFill(Color.valueOf("#eee"), null, null))

            children.apply {
                add(HBox().apply {
                    alignment = Pos.CENTER
                    prefWidth = 36.0
                    children.add(ImageView(Image(Main::class.java.getResourceAsStream("/knotbook/application/knot-tb.png"))).apply {
                        isPreserveRatio = true
                        fitHeight = 18.0
                    })
                })
                add(MenuBar(
                        Menu("File").apply {
                            items.addAll(
                                    Menu("New").apply {
                                        items.addAll(
                                                MenuItem("TBA Integration"),
                                                MenuItem("Python Integration"),
                                                MenuItem("Duplicate Table"),
                                                MenuItem("Derive Table")
                                        )
                                    },
                                    MenuItem("Open Repository", FontIcon.of(FontAwesomeSolid.FOLDER_OPEN, 16)).apply {
                                        accelerator = KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN)
                                    },
                                    MenuItem("Reveal Context in Source").apply {
                                        accelerator = KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN)
                                    },
                                    MenuItem("Rename Table").apply {
                                        accelerator = KeyCodeCombination(KeyCode.F6, KeyCombination.SHIFT_DOWN)
                                    },
                                    MenuItem("Synchronize", FontIcon.of(FontAwesomeSolid.SYNC, 16)).apply {
                                        accelerator = KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN)
                                    },
                                    MenuItem("Mark Repository As Read-Only", FontIcon.of(FontAwesomeSolid.LOCK, 16)).apply {
                                        accelerator = KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN)
                                    },
                                    MenuItem("Show Command Line Snippets", FontIcon.of(FontAwesomeSolid.CODE, 16)).apply {
                                        accelerator = KeyCodeCombination(KeyCode.BACK_QUOTE, KeyCombination.ALT_DOWN)
                                    },
                                    MenuItem("Delete").apply {
                                        accelerator = KeyCodeCombination(KeyCode.DELETE, KeyCombination.ALT_DOWN)
                                    },
                                    SeparatorMenuItem(),
                                    MenuItem("Exit")
                            )
                        },
                        Menu("View").apply {
                            items.addAll(
                                    MenuItem("Expand to Source").apply {
                                        accelerator = KeyCodeCombination(KeyCode.F9)
                                    },
                                    MenuItem("Toggle Sidebar").apply {
                                        accelerator = KeyCodeCombination(KeyCode.F9)
                                    },
                                    MenuItem("Toggle Fullscreen").apply {
                                        accelerator = KeyCodeCombination(KeyCode.F11)
                                    },
                                    MenuItem("Toggle Theme", FontIcon(FontAwesomeSolid.ADJUST)).apply {
                                        accelerator = KeyCodeCombination(KeyCode.F2)
                                    }
                            )
                        },
                        Menu("Help").apply {
                            items.addAll(
                                    MenuItem("Activity Monitor", FontIcon(FontAwesomeSolid.HEARTBEAT)),
                                    MenuItem("Plugin Manager", FontIcon(FontAwesomeSolid.CUBE)),
                                    MenuItem("Start Garbage Collection Cycle"),
                                    MenuItem("Application Registry"),
                                    SeparatorMenuItem(),
                                    MenuItem("About Knotbook").apply {
                                        accelerator = KeyCodeCombination(KeyCode.F1)
                                        onAction = EventHandler {
                                            Splash.splash()
                                        }
                                    }
                            )
                        }
                ).apply {

                })

                add(Label("Test Repo").apply {
                    style = "-fx-font-weight:bold"
                })
                add(Label(" - []"))
                add(HBox().apply { HBox.setHgrow(this, Priority.ALWAYS) })
                add(min)
                add(max)
                add(Button("", FontIcon.of(MaterialDesign.MDI_WINDOW_CLOSE, 17)).apply {
                    styleClass.add("close-button")
                    onAction = EventHandler {
                        stage.close()
                    }
                })
            }
        }
        stage.scene = SnapScene(stage, VBox().apply {
            stylesheets.add("/knotbook.css")
            prefWidth = 800.0
            prefHeight = 600.0
            children.add(mover)
            children.add(knotable)
        }).apply {
            setMoveControl(mover)
            min.onAction = EventHandler {
                minimise()
            }

            max.onAction = EventHandler {
                maximise()
            }
            Platform.runLater {
                maximise()
            }
        }

        knotable.requestFocus()
        stage.show()

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}