package knotbook.pathplanner

import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import knotbook.core.fx.draw

@Suppress("MemberVisibilityCanBePrivate", "unused")
class PathCanvas {

    val theCanvas = Canvas()

    inline fun draw(action: GraphicsContext.() -> Unit) {
        theCanvas.draw(action)
    }

    var p = Color.BLACK

    init {
        theCanvas.onMouseMoved = EventHandler {
            draw {
                fill = p
                fillOval(it.x, it.y, 5.0, 5.0)
            }
        }
        theCanvas.isFocusTraversable = true
        theCanvas.onMouseClicked = EventHandler {
            theCanvas.requestFocus()
        }
        theCanvas.onKeyPressed = EventHandler {
            p = Color.hsb(Math.random() * 360.0, 1.0, 1.0)
        }
        theCanvas.onMouseDragEntered = EventHandler {

        }
        theCanvas.onMouseDragExited = EventHandler {

        }
        theCanvas.onMouseDragOver = EventHandler {

        }
        theCanvas.onMouseDragReleased = EventHandler {

        }
    }
}