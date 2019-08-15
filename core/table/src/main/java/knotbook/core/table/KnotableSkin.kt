package knotbook.core.table

import javafx.geometry.*
import javafx.scene.control.ScrollBar
import javafx.scene.control.SkinBase
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.text.Text
import javafx.stage.Screen
import knotbook.core.table.virtual.VirtualGrid


@Suppress("MemberVisibilityCanBePrivate")
class KnotableSkin(knotable: Knotable) : SkinBase<Knotable>(knotable) {

    private companion object {
        const val kLineStroke = 0.5
        const val kMinCellWidth = 80.0
        const val kMinCellHeight = 18.0
        const val kTextMargin = 4.0
    }

    val grid = VirtualGrid()

    val hsb = ScrollBar().apply {
        orientation = Orientation.HORIZONTAL
        visibleAmount = 0.3
        min = 0.0
        max = 1.0
    }

    val vsb = ScrollBar().apply {
        orientation = Orientation.VERTICAL
        visibleAmount = 0.3
        min = 0.0
        max = 1.0
    }

//    val visualBounds: Rectangle2D = Screen.getPrimary().visualBounds
//
//    val vln: List<Line>
//    val hln: List<Line>
//
//    val cells: List<Text>

    init {
//        vln = (0..(visualBounds.width / kMinCellWidth).toInt()).map { Line() }
//        hln = (0..(visualBounds.width / kMinCellHeight).toInt()).map { Line() }
//        cells = (0 until vln.size * hln.size).map { Text("0") }
//        children.addAll(vln)
//        children.addAll(hln)
//        children.addAll(cells)
        children.addAll(hsb, vsb)
    }

    override fun computeMinWidth(height: Double,
                                 topInset: Double, rightInset: Double,
                                 bottomInset: Double, leftInset: Double): Double {
        return 300.0
    }

    override fun computeMinHeight(width: Double,
                                  topInset: Double, rightInset: Double,
                                  bottomInset: Double, leftInset: Double): Double {
        return 300.0
    }

    @Suppress("DuplicatedCode")
    override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
//        for (i in 0 until vln.size) {
//            val line = vln[i]
//            line.startX = 0.0
//            line.startY = contentHeight
//            line.endX = 0.0
//            line.endY = 0.0
//            line.strokeWidth = kLineStroke
//            line.stroke = Color.GRAY
//            val lineX = contentX + i * kMinCellWidth
//            layoutInArea(line, lineX, contentY,
//                    kMinCellWidth, contentHeight, -0.0,
//                    HPos.LEFT, VPos.TOP)
//        }
//
//        for (j in 0 until hln.size) {
//            val line = hln[j]
//            line.startX = contentWidth
//            line.startY = 0.0
//            line.endX = 0.0
//            line.endY = 0.0
//            line.strokeWidth = kLineStroke
//            line.stroke = Color.GRAY
//            val lineY = contentY + j * kMinCellHeight
//            layoutInArea(line, contentX, lineY,
//                    contentWidth, kMinCellHeight, -0.0,
//                    HPos.LEFT, VPos.TOP)
//        }
//
//        for (i in 0 until vln.size) {
//            for (j in 0 until hln.size) {
//                val cell = cells[i * hln.size + j]
//                val lineX = contentX + i * kMinCellWidth + kTextMargin
//                val lineY = contentY + j * kMinCellHeight
//                layoutInArea(cell, lineX, lineY, kMinCellWidth - 2 * kTextMargin,
//                        kMinCellHeight, 0.0, HPos.LEFT, VPos.CENTER)
//            }
//        }


        layoutInArea(hsb, 0.0, 0.0, contentWidth - vsb.width, contentHeight, 0.0,
                Insets.EMPTY, true, true, HPos.LEFT, VPos.BOTTOM)
        layoutInArea(vsb, 0.0, 0.0, contentWidth, contentHeight - hsb.height, 0.0,
                Insets.EMPTY, true, true, HPos.RIGHT, VPos.TOP)
    }
}