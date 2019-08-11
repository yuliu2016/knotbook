package knotbook.core.table.virtual

import javafx.scene.control.skin.VirtualFlow

/**
 * Pure math implementation of [VirtualFlow] that is independent of cells
 * or scroll bars; they can be handled in another class
 *
 * [VirtualView] represents the view of a 2D table
 */
@Suppress("unused", "SpellCheckingInspection")
class VirtualView(
        val minCellWidth: Int,
        val minCellHeight: Int
) {

    var rows = 0
    var columns = 0

    // The width and heights of individual cells

    var columnWidths = ArrayList<Double>()
    var rowHeights = ArrayList<Double>()


    var totalWidth = 0.0
    var totalHeight = 0.0

    // The width and height of the virtual box

    var clipWidth = 0.0
    var clipHeight = 0.0

    var zoomFactor = 1.0

    // The current position in [0, 1] representing MIN/MAX

    var x = 0.0
    var y = 0.0

    override fun toString(): String {
        return "VirtualView([$rows,$columns],[$totalWidth,$totalHeight],[$clipWidth,$clipHeight],$zoomFactor)"
    }

    companion object
}