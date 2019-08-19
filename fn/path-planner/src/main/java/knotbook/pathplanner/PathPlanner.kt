package knotbook.pathplanner

import javafx.scene.Scene
import javafx.stage.Stage
import knotbook.core.fx.vbox

@Suppress("MemberVisibilityCanBePrivate")
class PathPlanner {

    val stage = Stage()

    val scene = Scene(vbox {
        style = "-fx-background-color: black"
    })

    init {
        stage.title = "Path Planner"
        stage.scene = scene
        stage.show()
    }
}