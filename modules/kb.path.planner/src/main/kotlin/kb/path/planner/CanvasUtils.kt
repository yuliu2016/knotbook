package kb.path.planner

import javafx.scene.canvas.GraphicsContext
import kb.core.fx.draw
import javafx.event.EventHandler as handler

fun CanvasScope.initCanvas() {
    canvas.onMouseMoved = handler {
    }
    canvas.onMouseReleased = handler {
        mouseReleased()
    }
    canvas.onMouseClicked = handler {
    }
    canvas.onKeyPressed = handler {
        keyPressed()
    }
    canvas.onKeyReleased = handler {
        keyReleased()
    }
    canvas.onMouseDragEntered = handler {
    }
    canvas.onMouseDragExited = handler {
    }
    canvas.onMouseDragOver = handler {
    }
    canvas.onMouseDragReleased = handler {
    }
}

inline fun CanvasScope.draw(action: GraphicsContext.() -> Unit) {
    canvas.draw(action)
}