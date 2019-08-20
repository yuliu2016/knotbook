package knotbook.pathplanner

import ca.warp7.frc.drive.DynamicState
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.drive.trajectory.TrajectoryState
import ca.warp7.frc.geometry.*
import ca.warp7.frc.path.QuinticSegment2D
import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import knotbook.core.fx.draw
import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate", "unused")
class PathCanvas: CanvasScope {

    override val theCanvas = Canvas()

    inline fun draw(action: GraphicsContext.() -> Unit) {
        theCanvas.draw(action)
    }

    var p = Color.BLACK

    val kTriangleRatio = 1 / sqrt(3.0)
    val step = 0.0254 * 12 / 10
    val pAng = Rotation2D.fromDegrees(1.0)
    val nAng = Rotation2D.fromDegrees(-1.0)
    val reversedRotation = Rotation2D(-1.0, 0.0)

    val wheelBaseRadius = kInchesToMeters * 12.4
    val robotLength = wheelBaseRadius * 1.3
    val robotDrawCenter = Translation2D(768.0, 100.0)

    val kPixelsPerMeter = 494 / 8.2296
    val yCenterPx = 17.0 + (512.0 - 17.0) / 2
    val Double.my2x: Double get() = (yCenterPx - kPixelsPerMeter * this)
    val Double.mx2y: Double get() = (493.0 - kPixelsPerMeter * this)
    val Double.px2y: Double get() = (yCenterPx - this) / kPixelsPerMeter
    val Double.py2x: Double get() = (493.0 - this) / kPixelsPerMeter
    val Translation2D.newXY get() = Translation2D(y.my2x, x.mx2y)
    val Translation2D.newXYNoOffset get() = Translation2D(-kPixelsPerMeter * y, -kPixelsPerMeter * x)
    val Translation2D.oldXY get() = Translation2D(y.py2x, x.px2y)
    val Translation2D.oldXYNoOffset get() = Translation2D(-y / kPixelsPerMeter, -x / kPixelsPerMeter)

    var waypoints: Array<Pose2D> = emptyArray()
    var intermediate: List<QuinticSegment2D> = emptyList()
    var splines: List<ArcPose2D> = emptyList()
    var trajectory: List<TrajectoryState> = emptyList()
//    var controlPoints: MutableList<ControlPoint> = mutableListOf()
    var dynamics: List<Triple<WheelState, DynamicState, Double>> = emptyList()

    var maxVRatio = 1.0
    var maxARatio = 1.0
    var maxAcRatio = 1.0
    var jerkLimiting = false
    var optimizing = false

    var curvatureSum = 0.0
    var arcLength = 0.0
    var trajectoryTime = 0.0
    var maxK = 0.0
    var maxAngular = 0.0
    var maxAngularAcc = 0.0

    var selectedIndex = -1
    var selectionChanged = false

    init {
        initCanvas()
    }
}