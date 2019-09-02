package kb.core.bowline

import javafx.scene.Scene
import javafx.stage.Stage

fun testBowline() {
    val stage = Stage()
    stage.title = "Bowline Test"
    stage.scene = Scene(Bowline())
    stage.width = 900.0
    stage.height = 600.0
    stage.show()
}