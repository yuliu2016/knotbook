package kb.core.fx

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext

@FXKtDSL
inline fun Canvas.draw(builder: GraphicsContext.() -> Unit) {
    graphicsContext2D.apply(builder)
}