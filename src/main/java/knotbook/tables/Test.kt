package knotbook.tables

import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import knotbook.core.snap.SnapScene
import knotbook.core.splash.Splash
//import knotbook.core.splash.Splash
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign


class Test : Application() {
    override fun start(stage: Stage) {
        stage.title = "Knotable"
//        val root = Pane()
//        root.isSnapToPixel = true
//        val canvas = KnotableSurface()
//
//        canvas.widthProperty().bind(root.widthProperty())
//        canvas.heightProperty().bind(root.heightProperty())
//
//        root.prefWidth = 800.0
//        root.prefHeight = 800.0
//
//        root.children.add(canvas)
        stage.icons.add(Image(Test::class.java.getResourceAsStream("/knot-tb.png")))
        val knotable = Knotable()
        knotable.prefWidth = 900.0
        knotable.prefHeight = 600.0
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
                    children.add(ImageView(Image(Test::class.java.getResourceAsStream("/knot-tb.png"))).apply {
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
                                    MenuItem("Toggle Sidebar", FontIcon(FontAwesomeSolid.ADJUST)).apply {
                                        accelerator = KeyCodeCombination(KeyCode.F9)
                                    },
                                    MenuItem("Toggle Fullscreen", FontIcon(FontAwesomeSolid.ADJUST)).apply {
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

                add(Label("Test Project").apply {
                    style = "-fx-font-weight:bold"
                })
                add(Label(" - Knotbook"))
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
        }
        knotable.requestFocus()
        stage.show()
    }

    fun showAbout() {
        val s2 = Stage()
        val sc = Scene(VBox().apply {
            style = "-fx-background-color:transparent"
            children.add(VBox().apply {
                padding = Insets(10.0)
                style = "-fx-background-color:rgba(96,96,96,0.9)"
                alignment = Pos.CENTER
                children.add(HBox().apply {
                    alignment = Pos.BASELINE_CENTER
                    prefHeight = 80.0
                    children.addAll(
                            ImageView(Image(Test::class.java.getResourceAsStream("/knot-tb.png"))).apply {
                                isPreserveRatio = true
                                fitHeight = 80.0
                            },
                            Label("notbook").apply {
                                style = "-fx-font-size: 72;-fx-font-weight:bold;-fx-text-fill: white;-fx-line-height:1"
                            }
                    )
                })
                children.add(Label("Powered by Knotable - Version 2019.2.0").apply {
                    style = "-fx-text-fill: white"
                })

            })
            children.add(VBox().apply {
                style = "-fx-background-color:rgba(0,0,0,0.9)"
                VBox.setVgrow(this, Priority.ALWAYS)
                alignment = Pos.TOP_CENTER
                padding = Insets(8.0)

            })
            prefWidth = 500.0
            prefHeight = 340.0
        })
        sc.fill = Color.TRANSPARENT
        s2.focusedProperty().addListener { _, _, newValue ->
            if (!newValue) s2.close()
        }
        s2.scene = sc
        s2.initStyle(StageStyle.TRANSPARENT)
        s2.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Test::class.java)
        }
    }
}