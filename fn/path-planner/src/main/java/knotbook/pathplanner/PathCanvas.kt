package knotbook.pathplanner

import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.DynamicState
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.drive.trajectory.TrajectoryState
import ca.warp7.frc.drive.trajectory.generateTrajectory
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.feet
import ca.warp7.frc.geometry.*
import ca.warp7.frc.linearInterpolate
import ca.warp7.frc.path.QuinticSegment2D
import ca.warp7.frc.path.parameterized
import ca.warp7.frc.path.quinticSplinesOf
import ca.warp7.frc.path.sumDCurvature2
import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.paint.Color
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate", "unused")
class PathCanvas : CanvasScope {

    override val canvas = Canvas()

    private val bg = Image(PathCanvas::class.java.getResourceAsStream("/field.PNG"))

    val kTriangleRatio = 1 / sqrt(3.0)
    val step = 0.0254 * 12 / 10
    val pAng = Rotation2D.fromDegrees(1.0)
    val nAng = Rotation2D.fromDegrees(-1.0)
    val reversedRotation = Rotation2D(-1.0, 0.0)

    val wheelBaseRadius = kInchesToMeters * 12.4
    val robotLength = wheelBaseRadius * 1.3
    val robotDrawCenter = Translation2D(768.0, 100.0)

    val kPixelsPerMeter = 494 / 8.2296
    val yCenterPx = 493.0 / 2.0

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

    var draggingPoint = false
    var draggingAngle = false

    var simulating = false
    var simPaused = false
    var simElapsed = 0.0
    var simIndex = 0
    var simElapsedChanged = false

    var lastTime = 0.0
    var dt = 0.0


    val model = DifferentialDriveModel(
            wheelRadius = kWheelRadius,
            wheelbaseRadius = kEffectiveWheelBaseRadius,
            maxVelocity = kMaxVelocity,
            maxAcceleration = kMaxAcceleration,
            maxFreeSpeed = kMaxFreeSpeed,
            speedPerVolt = kSpeedPerVolt,
            torquePerVolt = kTorquePerVolt,
            frictionVoltage = kFrictionVoltage,
            linearInertia = kLinearInertia,
            angularInertia = kAngularInertia,
            maxVoltage = kMaxVolts,
            angularDrag = kAngularDrag
    )

    init {
        initCanvas()
        waypoints = arrayOf(
                Pose2D(6.feet, 4.feet, 0.degrees),
                Pose2D(16.8.feet, 11.2.feet, 32.degrees)
        )
        canvas.requestFocus()
        canvas.onMouseClicked = EventHandler {
            regenerate()
        }
    }

    fun regenerate() {
//        controlPoints.clear()
//        waypoints.forEach {
//            val pos = it.translation.newXY
//            val heading = (it.translation + it.rotation.translation.scaled(0.5)).newXY
//            val dir = it.rotation.norm.translation
//            controlPoints.add(ControlPoint(pos, heading, dir))
//        }
        intermediate = quinticSplinesOf(*waypoints, optimizePath = optimizing)
        curvatureSum = intermediate.sumDCurvature2()
        splines = intermediate.parameterized()
        arcLength = splines.zipWithNext { a: ArcPose2D, b: ArcPose2D ->
            val chordLength = (a.translation - b.translation).mag
            if (a.curvature.epsilonEquals(0.0)) chordLength else
                kotlin.math.abs(kotlin.math.asin(chordLength * a.curvature / 2) / a.curvature * 2)
        }.sum()
        trajectory = generateTrajectory(splines, model.wheelbaseRadius,
                model.maxVelocity * maxVRatio,
                model.maxAcceleration * maxARatio,
                model.maxAcceleration * maxAcRatio,
                if (jerkLimiting) 45.0 else Double.POSITIVE_INFINITY)
        trajectoryTime = trajectory.last().t
        maxK = splines.maxBy { it.curvature.absoluteValue }?.curvature?.absoluteValue ?: 1.0
        maxAngular = trajectory.map { kotlin.math.abs(it.w) }.max() ?: 1.0
        maxAngularAcc = trajectory.map { kotlin.math.abs(it.dw) }.max() ?: 1.0
        redrawScreen()
    }

    fun redrawBackground() {
        draw {
            fill = kBlack
            fillRect(0.0, 0.0, canvas.width, canvas.height)
            drawImage(bg, 0.0, 0.0, 495.0, 493.0)
        }
    }

    fun drawArrow(point: Pose2D) {
        draw {
            val pos = point.translation.newXY
            val heading = (point.translation + point.rotation.translation.scaled(0.5)).newXY
            val dir = point.rotation.norm.translation

            val r1 = dir.scaled(0.1524 * kTriangleRatio * 2)
            val r2 = r1.rotate(Rotation2D(0.0, 1.0)).scaled(kTriangleRatio)
            val r3 = r1.rotate(Rotation2D(0.0, -1.0)).scaled(kTriangleRatio)
            strokeOval(pos.x - 6.0, pos.y - 6.0, 12.0, 12.0)
            val start = pos - dir.scaled(6.0).transposed
            lineTo(start, heading)
            val a1 = heading + r1.newXYNoOffset
            val a2 = heading + r2.newXYNoOffset
            val a3 = heading + r3.newXYNoOffset

            beginPath()
            vertex(a1)
            vertex(a2)
            vertex(a3)
            vertex(a1)
            closePath()
            stroke()
        }
    }

    fun redrawControlPoints() {
        draw {
            waypoints.forEachIndexed { index, waypoint ->
                stroke = if (selectedIndex == index) {
                    kSelectedControlPointColor
                } else {
                    kControlPointColor
                }
                lineWidth = 2.0
                drawArrow(waypoint)
            }
        }
    }

    fun v2T(v: Double, t: Double, max: Double, y: Int) =
            Translation2D(531 + (t / trajectoryTime) * 474, y - (v / max) * 50)

    fun v2T2(v: Double, t: Double, max: Double, y: Int) =
            Translation2D(531 + (t / trajectoryTime) * 474, y - (v / max) * 100)

    fun h2TL(v: Double, t: Double, max: Double, y: Int) =
            Translation2D(531 + (t / trajectoryTime) * 231, y - (v / max) * 104)

    fun h2TR(v: Double, t: Double, max: Double, y: Int) =
            Translation2D(774 + (t / trajectoryTime) * 231, y - (v / max) * 104)

    fun List<Translation2D>.connect() {
        draw {
            beginPath()
            forEach { vertex(it) }
            closePath()
            stroke()
        }
    }

    fun drawGraph(i: Int) {
        if (i == 0) return
        draw {
            trajectory.subList(0, i + 1).apply {
                lineWidth = 1.0
                stroke = Color.rgb(255, 128, 0)
                forEachIndexed { index, point ->
                    val x = 0 + (point.t / trajectoryTime) * 512
                    if (index % 2 == 0) strokeLine(x, 652.5, x, 667.5)
                    else strokeLine(x, 657.5, x, 672.5)
                }
                lineWidth = 2.0
                stroke = Color.rgb(0, 128, 192)
                map { v2T(it.dv, it.t, model.maxAcceleration, 434) }.connect()
                //stroke(0f, 192f, 128f)
                //map { v2T((it.state.curvature * it.acceleration), it.t, maxAngularAcc, 434) }.connect()
                stroke = Color.rgb(255, 255, 128)
                map { v2T(it.w, it.t, maxAngular, 290) }.connect()
                stroke = Color.rgb(128, 128, 255)
                map { v2T2(it.v, it.t, model.maxVelocity, 340) }.connect()
            }
        }
    }

    fun redrawScreen() {
        redrawBackground()

        draw {
            // draw the start of the curve
            val s0 = splines.first()
            val t0 = s0.translation
            var normal = (s0.rotation.normal * wheelBaseRadius).translation
            var left = (t0 - normal).newXY
            var right = (t0 + normal).newXY

            lineWidth = 2.0
            stroke = kGreen
            val a0 = t0.newXY - Translation2D(robotLength, wheelBaseRadius).rotate(s0.rotation).newXYNoOffset
            val b0 = t0.newXY + Translation2D(-robotLength, wheelBaseRadius).rotate(s0.rotation).newXYNoOffset
            lineTo(a0, b0)
            lineTo(left, a0)
            lineTo(right, b0)

            // draw the curve
            for (i in 1 until splines.size) {
                val s = splines[i]
                val t = s.translation
                normal = (s.rotation.normal * wheelBaseRadius).translation
                val newLeft = (t - normal).newXY
                val newRight = (t + normal).newXY
                val kx = s.curvature.absoluteValue / maxK
                val r = linearInterpolate(0.0, 192.0, kx) + 63
                val g = 255 - linearInterpolate(0.0, 192.0, kx)
                stroke = Color.rgb(r.toInt(), g.toInt(), 0)
                lineTo(left, newLeft)
                lineTo(right, newRight)
                left = newLeft
                right = newRight
            }

            // draw the end of the curve

            val sf = splines.last()
            stroke = kGreen
            val tf = sf.translation.newXY
            val af = tf - Translation2D(-robotLength, wheelBaseRadius).rotate(sf.rotation).newXYNoOffset
            val bf = tf + Translation2D(robotLength, wheelBaseRadius).rotate(sf.rotation).newXYNoOffset
            lineTo(af, bf)
            lineTo(left, af)
            lineTo(right, bf)

            if (!simulating) {
                redrawControlPoints()
                drawGraph(trajectory.size - 1)
            }
        }
    }

}