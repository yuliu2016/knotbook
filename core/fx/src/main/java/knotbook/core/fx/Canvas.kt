package knotbook.core.fx

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext

@FXKtDSL
fun Canvas.draw(builder: GraphicsContext.() -> Unit) {
    graphicsContext2D.apply(builder)
}