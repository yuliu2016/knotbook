package kb.core.bowline

import javafx.event.EventHandler
import javafx.geometry.*
import javafx.scene.canvas.Canvas
import javafx.scene.control.ScrollBar
import javafx.scene.control.SkinBase
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import javafx.scene.text.Text
import javafx.stage.Screen
import kotlin.math.roundToInt
import kotlin.system.exitProcess


@Suppress("MemberVisibilityCanBePrivate")
internal class BowlineSkin(bowlineTable: BowlineTable) :
    SkinBase<BowlineTable>(bowlineTable) {

    private companion object {
        const val kLineStroke = 0.5
        const val kMinCellWidth = 72.0
        const val kMinCellHeight = 18.0
        const val kTextMargin = 4.0
    }

    val flow = GridVirtualFlow()

    // Scroll bars are set to a range of [0,1] instead of pixels

    val hsb = ScrollBar().apply {
        orientation = Orientation.HORIZONTAL
        visibleAmount = 1.0
        min = 0.0
        max = 1.0
    }

    val vsb = ScrollBar().apply {
        orientation = Orientation.VERTICAL
        visibleAmount = 1.0
        min = 0.0
        max = 1.0
    }

    val visualBounds: Rectangle2D = Screen.getPrimary().visualBounds

    val vln: List<Line>
    val hln: List<Line>

    val cells: List<Text>

    val canvas = Canvas()

    val rangeRect = Rectangle()

    init {
        flow.x.minSize = kMinCellWidth
        flow.y.minSize = kMinCellHeight
        flow.x.setCellCount(20)
        flow.y.setCellCount(300)
        vln = (0..(visualBounds.width / kMinCellWidth).toInt()).map { Line() }
        hln = (0..(visualBounds.height / kMinCellHeight).toInt()).map { Line() }
        cells = (0 until vln.size * hln.size).map { Text(it.toString()) }

        children.add(canvas)
        children.addAll(vln)
        children.addAll(hln)
        children.addAll(cells)

        children.addAll(rangeRect, hsb, vsb)

        hsb.valueProperty().addListener { _, _, newValue ->
            flow.x.scrollTo(newValue.toDouble().coerceIn(-1.0, 1.0))
            hsb.visibleAmount = flow.x.thumbSize()
            skinnable.requestLayout()
        }

        vsb.valueProperty().addListener { _, _, newValue ->
            flow.y.scrollTo(newValue.toDouble().coerceIn(-1.0, 1.0))
            vsb.visibleAmount = flow.y.thumbSize()
            skinnable.requestLayout()
        }

        skinnable!!.apply {
            val clipRect = Rectangle(0.0, 0.0)

            clipRect.heightProperty().bind(heightProperty())
            clipRect.widthProperty().bind(widthProperty())

            canvas.widthProperty().bind(widthProperty())
            canvas.heightProperty().bind(heightProperty())

            clip = clipRect

            onScroll = EventHandler {
                vsb.value = flow.y.scrollByAndGet(it.deltaY * 2)
                hsb.value = flow.x.scrollByAndGet(it.deltaX * 2)
            }
        }
    }

    override fun computeMinWidth(
        height: Double,
        topInset: Double, rightInset: Double,
        bottomInset: Double, leftInset: Double
    ): Double {
        return 300.0
    }

    override fun computeMinHeight(
        width: Double,
        topInset: Double, rightInset: Double,
        bottomInset: Double, leftInset: Double
    ): Double {
        return 300.0
    }

    @Suppress("DuplicatedCode")
    override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
        flow.x.updateContentClip(contentWidth)
        flow.y.updateContentClip(contentHeight)

        val gc = canvas.graphicsContext2D
        gc.fill = Color.WHITE
        gc.fillRect(0.0, 0.0, contentWidth, contentHeight)

        gc.fill = Color.rgb(240, 240, 240)
        gc.fillRect(0.0, 0.0, contentWidth, kMinCellHeight)
        gc.fill = Color.rgb(0, 144, 0)
        gc.fillRect(kMinCellWidth *4, kMinCellHeight * 3, kMinCellWidth, kMinCellHeight)

        flow.x.doIfStateChanged {
            for (i in vln.indices) {
                val line = vln[i]
                if (i < flow.x.virtualCellCount) {
                    val x = flow.x.virtualCellPos[i]
                    line.startX = 0.0
                    line.startY = 0.0
                    line.endX = 0.0
                    line.endY = contentHeight
                    line.strokeWidth = kLineStroke
                    line.stroke = Color.GRAY
                    line.isVisible = true
                    line.isSmooth = false
                    line.relocate(snapPositionX(x), contentY)
                } else {
                    line.isVisible = false
                }
            }
        }

        flow.y.doIfStateChanged {
            for (j in hln.indices) {
                val line = hln[j]
                if (j < flow.y.virtualCellCount) {
                    val y = flow.y.virtualCellPos[j]
                    line.startX = 0.0
                    line.startY = 0.0
                    line.endX = contentWidth
                    line.endY = 0.0
                    line.strokeWidth = kLineStroke
                    line.stroke = Color.GRAY
                    line.isVisible = true
                    line.isSmooth = false
                    line.relocate(contentX, snapPositionY(y))
                } else {
                    line.isVisible = false
                }
            }
        }


        rangeRect.width = kMinCellWidth * 3
        rangeRect.height = kMinCellHeight * 7
        rangeRect.fill = Color.rgb(128, 128, 128, 0.15)
//        rangeRect.fill = null
        rangeRect.isSmooth = false
        rangeRect.stroke = Color.valueOf("#3c5c94")
        rangeRect.strokeWidth = 1.5
        // 2 ->1.5
        // 3 -> 1.75

        rangeRect.relocate(kMinCellWidth * 4, kMinCellHeight * 10)
//
//        layoutInArea(rangeRect,  kMinCellWidth * 4, kMinCellHeight * 10,
//            contentWidth, contentHeight, 0.0, HPos.LEFT, VPos.TOP )

        for (i in vln.indices) {
            for (j in hln.indices) {
                val cell = cells[i * hln.size + j]
                val lineX = contentX + i * kMinCellWidth + kTextMargin
                val lineY = contentY + j * kMinCellHeight
                layoutInArea(
                    cell, lineX, lineY, kMinCellWidth - 2 * kTextMargin,
                    kMinCellHeight, 0.0, HPos.RIGHT, VPos.CENTER
                )
            }
        }

        layoutInArea(
            hsb, 0.0, 0.0, contentWidth - vsb.width,
            contentHeight, 0.0,
            Insets.EMPTY, true, true, HPos.LEFT, VPos.BOTTOM
        )

        layoutInArea(
            vsb, 0.0, 0.0, contentWidth,
            contentHeight - kMinCellHeight, 0.0,
            Insets.EMPTY, true, true, HPos.RIGHT, VPos.TOP
        )
    }
}