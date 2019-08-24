package knotbook.core.table

import javafx.event.EventHandler
import javafx.geometry.*
import javafx.scene.control.ScrollBar
import javafx.scene.control.SkinBase
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.text.Text
import javafx.stage.Screen


@Suppress("MemberVisibilityCanBePrivate")
class KnotTableSkin(knotable: KnotTable) : SkinBase<KnotTable>(knotable) {

    private companion object {
        const val kLineStroke = 0.5
        const val kMinCellWidth = 90.0
        const val kMinCellHeight = 18.0
        const val kTextMargin = 4.0
    }

    val flow = GridVirtualFlow()

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

    init {
        flow.x.minSize = kMinCellWidth
        flow.y.minSize = kMinCellHeight
        flow.x.setCellCount(20)
        flow.y.setCellCount(700)
        vln = (0..(visualBounds.width / kMinCellWidth).toInt()).map { Line() }
        hln = (0..(visualBounds.height / kMinCellHeight).toInt()).map { Line() }
        cells = (0 until vln.size * hln.size).map { Text("0") }
        children.addAll(vln)
        children.addAll(hln)
        children.addAll(cells)
        children.addAll(hsb, vsb)
        skinnable!!.apply {
            onScroll = EventHandler {
                flow.x.scrollBy(it.deltaX * 2)
                flow.y.scrollBy(it.deltaY * 2)
                vsb.value = flow.y.scroll
                hsb.value = flow.x.scroll
                vsb.visibleAmount = flow.y.thumbSize()
                hsb.visibleAmount = flow.x.thumbSize()
                requestLayout()
            }
        }
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
        flow.x.updateContentClip(contentWidth)
        flow.y.updateContentClip(contentHeight)
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
                    layoutInArea(line, x, contentY, 2.0, contentHeight, 0.0, HPos.LEFT, VPos.TOP)
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
                    layoutInArea(line, contentX, y,
                            contentWidth, 2.0, 0.0,
                            HPos.LEFT, VPos.TOP)
                } else {
                    line.isVisible = false
                }
            }
        }

        for (i in vln.indices) {
            for (j in hln.indices) {
                val cell = cells[i * hln.size + j]
                val lineX = contentX + i * kMinCellWidth + kTextMargin
                val lineY = contentY + j * kMinCellHeight
                layoutInArea(cell, lineX, lineY, kMinCellWidth - 2 * kTextMargin,
                        kMinCellHeight, 0.0, HPos.LEFT, VPos.CENTER)
            }
        }

        layoutInArea(hsb, 0.0, 0.0, contentWidth - vsb.width, contentHeight, 0.0,
                Insets.EMPTY, true, true, HPos.LEFT, VPos.BOTTOM)
        layoutInArea(vsb, 0.0, 0.0, contentWidth, contentHeight - hsb.height, 0.0,
                Insets.EMPTY, true, true, HPos.RIGHT, VPos.TOP)
    }
}