package knotbook.pathplanner

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
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

    val scene = Scene(hbox {
        add(mainCanvas)
        add(vbox {
            prefWidth = 400.0
            spacing = 8.0
            padding = Insets(8.0)
            add(hbox {
                align(Pos.CENTER_LEFT)
                spacing = 8.0
                add(label { text = "Path Type: " })
                add(choiceBox<String> {
                    items.setAll(
                            "Quintic Hermite Splines",
                            "Cubic Hermite Splines",
                            "Bezier Splines",
                            "Line and Arc"
                    )
                    selectionModel.select(0)
                })
            })
            add(hbox {
                align(Pos.CENTER_LEFT)
                spacing = 8.0
                add(label { text = "Bend Factor: " })
                add(textField {  })
            })
            add(hbox {
                align(Pos.CENTER_LEFT)
                spacing = 8.0
                add(label { text = "Max Velocity: " })
                add(textField {  })
                add(slider {  })
            })

            add(checkbox {
                text = "Ramped Acceleration Pass"
            })
            add(checkbox {
                text = "Iterative Δk² Optimization"
            })
        })
    })

    init {
        stage.title = "Path Planner"
        stage.scene = scene
        stage.show()
    }
}