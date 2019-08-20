package knotbook.pathplanner

import javafx.scene.canvas.Canvas

interface CanvasScope {
    val theCanvas: Canvas

    fun keyPressed() {
    }

    fun keyReleased() {
    }

    fun mouseReleased() {
    }
}