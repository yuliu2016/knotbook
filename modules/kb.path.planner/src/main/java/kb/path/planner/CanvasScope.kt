package kb.path.planner

import javafx.scene.canvas.Canvas

interface CanvasScope {
    val canvas: Canvas

    fun keyPressed() {
    }

    fun keyReleased() {
    }

    fun mouseReleased() {
    }
}