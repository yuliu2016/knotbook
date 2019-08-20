package knotbook.pathplanner

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import knotbook.core.fx.*

@Suppress("MemberVisibilityCanBePrivate")
class PathPlanner {

    val stage = Stage()

    val mainCanvas = canvas {
        width = 512.0
        height = 768.0
        draw {
            fill = Color.BLACK
            fillRect(0.0, 0.0, width, height)
        }
    }

    @Suppress("UNUSED_CHANGED_VALUE")
    val scene = Scene(hbox {
        prefWidth = 1012.0
        maxHeight = 800.0
        add(mainCanvas)
        add(scrollPane {
            isVisible = false
            onScroll = EventHandler {
                val deltaY = it.deltaY * 8 // *6 to make the scrolling a bit faster
                val width = content.boundsInLocal.width
                val value = vvalue
                vvalue = value + -deltaY / width // deltaY/width to make the scrolling equally fast regardless of the actual width of the component

            }
            prefWidth = 400.0
            minWidth = 400.0
            vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
            isFitToWidth = true
            content = gridPane {
                style = "-fx-background-color: white"
                padding = Insets(8.0)
                hgap = 8.0
                vgap = 4.0
                var r = 0
                add(label {
                    text = "Field Model Configuration"
                    padding = Insets(8.0, 0.0, 0.0, 0.0)
                    style = "-fx-font-weight:bold; -fx-font-size:14"
                }, 0, r++, 2, 1)
                addRow(r++, label { text = "Horizontal Origin" }, choiceBox<String> {
                    items.setAll(
                            "Left",
                            "Centre",
                            "Right"
                    )
                    selectionModel.select(1)
                })
                addRow(r++, label { text = "Vertical Origin" }, choiceBox<String> {
                    items.setAll(
                            "Top",
                            "Centre",
                            "Bottom"
                    )
                    selectionModel.select(2)
                })
                addRow(r++, label { text = "Presets" }, choiceBox<String> {
                    items.setAll(
                            "FRC 2019",
                            "FRC 2018"
                    )
                })
                addRow(r++, label { text = "Background Image" }, Button("Choose"))
                addRow(r++, label { text = "Pixels per Meter" }, textField {  })
                addRow(r++, label { text = "Point Model" }, textField {  })
                add(label {
                    text = "Path Configuration"
                    padding = Insets(8.0, 0.0, 0.0, 0.0)
                    style = "-fx-font-weight:bold; -fx-font-size:14"
                }, 0, r++, 2, 1)
                addRow(r++, label { text = "Heading Conform Factor" }, textField {  })
                addRow(r++, label { text = "Enable Quintic Splines" }, checkbox {  })
                addRow(r++, label { text = "Enable Cubic Splines" }, checkbox {  })
                addRow(r++, label { text = "Enable Arcs" }, checkbox {  })
                addRow(r++, label { text = "Enable Turning In Place" }, checkbox {  })
                addRow(r++, label { text = "Minimum Segment dX" }, textField {  })
                addRow(r++, label { text = "Minimum Segment dY" }, textField {  })
                addRow(r++, label { text = "Minimum Segment dTheta" }, textField {  })
                addRow(r++, label { text = "Iterative Δk² Optimization" }, checkbox {  })
                addRow(r++, label { text = "Optimization Passes" }, textField {  })
                add(label {
                    text = "Robot Configuration"
                    padding = Insets(8.0, 0.0, 0.0, 0.0)
                    style = "-fx-font-weight:bold; -fx-font-size:14"
                }, 0, r++, 2, 1)
                addRow(r++, label { text = "Effective Wheelbase Radius" }, textField {  })
                addRow(r++, label { text = "Wheelbase Multiplier" }, slider {  })
                addRow(r++, label { text = "Wheel Radius" }, textField {  })
                addRow(r++, label { text = "kV" }, textField {  })
                addRow(r++, label { text = "kA" }, textField {  })

                add(label {
                    text = "Trajectory Configuration"
                    padding = Insets(8.0, 0.0, 0.0, 0.0)
                    style = "-fx-font-weight:bold; -fx-font-size:14"
                }, 0, r++, 2, 1)
                addRow(r++, label { text = "Max Velocity" }, textField {  })
                addRow(r++, label { text = "Max Velocity Multiplier" }, slider {  })
                addRow(r++, label { text = "Max Acceleration" }, textField {  })
                addRow(r++, label { text = "Max Acceleration Multiplier" }, slider {  })
                addRow(r++, label { text = "Max Centripetal Acceleration" }, textField {  })
                addRow(r++, label { text = "Max Centripetal Multiplier" }, slider {  })
                addRow(r++, label { text = "Max Jerk" }, textField {  })
                addRow(r++, label { text = "Max Jerk Multiplier" }, slider {  })
                addRow(r++, label { text = "Ramped Acceleration Pass" }, checkbox {  })

                add(label {
                    text = "Rendering"
                    padding = Insets(8.0, 0.0, 0.0, 0.0)
                    style = "-fx-font-weight:bold; -fx-font-size:14"
                }, 0, r++, 2, 1)

                addRow(r++, label { text = "Show Background" }, checkbox {  })
                addRow(r++, label { text = "Show Curvature Gradients" }, checkbox {  })
                addRow(r++, label { text = "Robot Width" }, textField {  })
                addRow(r++, label { text = "Robot Length" }, textField {  })

                add(label {
                    text = "Plot"
                    padding = Insets(8.0, 0.0, 0.0, 0.0)
                    style = "-fx-font-weight:bold; -fx-font-size:14"
                }, 0, r++, 2, 1)

                addRow(r++, label { text = "Velocity" }, checkbox {  })
                addRow(r++, label { text = "Acceleration" }, checkbox {  })
                addRow(r++, label { text = "Jerk" }, checkbox {  })
                addRow(r++, label { text = "Angular Velocity" }, checkbox {  })
                addRow(r++, label { text = "Angular Acceleration" }, checkbox {  })
                addRow(r++, label { text = "Angular Jerk" }, checkbox {  })
                addRow(r++, label { text = "Time Steps" }, checkbox {  })
                addRow(r++, label { text = "Time Steps" }, checkbox {  })


                add(label {
                    text = "Simulation"
                    padding = Insets(8.0, 0.0, 0.0, 0.0)
                    style = "-fx-font-weight:bold; -fx-font-size:14"
                }, 0, r++, 2, 1)

                addRow(r++, label { text = "Show Curvature Circle" }, checkbox {  })
                addRow(r++, label { text = "Small Incremental Step" }, textField {  })
                addRow(r++, label { text = "Large Incremental Step" }, textField {  })
                addRow(r++, label { text = "Pause on Space Key" }, checkbox {  })
            }
        })
    })

    init {
        stage.title = "Differential Drive Path/Trajectory Planner"
        stage.scene = scene
        stage.heightProperty().addListener { _, _, newValue ->
            mainCanvas.height = newValue.toDouble()
            mainCanvas.width = newValue.toDouble() / 3.0 * 2.0
            mainCanvas.draw {
                fill = Color.BLACK
                fillRect(0.0, 0.0, mainCanvas.width, mainCanvas.height)
            }
        }
        stage.maxWidth = Screen.getPrimary().visualBounds.height / 3.0 * 2.0 + 400.0
        stage.show()
    }
}