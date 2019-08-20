package knotbook.pathplanner

import ca.warp7.frc.geometry.Translation2D
import javafx.scene.canvas.GraphicsContext


fun GraphicsContext.lineTo(a: Translation2D, b: Translation2D) = strokeLine(a.x, a.y, b.x, b.y)
fun GraphicsContext.vertex(a: Translation2D) = moveTo(a.x, a.y)
