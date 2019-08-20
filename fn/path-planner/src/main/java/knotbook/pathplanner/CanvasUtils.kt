package knotbook.pathplanner

import javafx.scene.canvas.GraphicsContext
import knotbook.core.fx.draw
import javafx.event.EventHandler as handler

fun CanvasScope.initCanvas() {
    theCanvas.onMouseMoved = handler {
    }
    theCanvas.onMouseReleased = handler {
        mouseReleased()
    }
    theCanvas.onMouseClicked = handler {
    }
    theCanvas.onKeyPressed = handler {
    }
    theCanvas.onMouseDragEntered = handler {
    }
    theCanvas.onMouseDragExited = handler {
    }
    theCanvas.onMouseDragOver = handler {
    }
    theCanvas.onMouseDragReleased = handler {
    }
}

inline fun CanvasScope.draw(action: GraphicsContext.() -> Unit) {
    theCanvas.draw(action)
}