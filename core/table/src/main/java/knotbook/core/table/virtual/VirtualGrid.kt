package knotbook.core.table.virtual

/**
 * Pure implementation of VirtualFlow that is independent of cells
 * or scroll bars; those can be handled in another class
 *
 * [VirtualGrid] represents the view of a 2D grid. It doesn't include
 * row/column headers because those can be treated like grid cells
 * position-wise
 *
 * The class also includes some fine tuned calculations for
 * JavaFX layout
 */
@Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate")
class VirtualGrid {

    // Minimum and maximum bounds for the grid

    var minMax = GridMinMax()

    // The size of the grid to display

    var rows = 0
        private set
    var columns = 0
        private set

    // The width and heights of individual cells

    var columnWidths = DoubleArray(0)
        private set
    var rowHeights = DoubleArray(0)
        private set

    // The total dimensions of the entire grid
    // Equal to the sum of the individual dimensions up to the specified size

    var totalWidth = 0.0
        private set
    var totalHeight = 0.0
        private set

    // The width and height of the virtual box

    var clipWidth = 0.0
        private set
    var clipHeight = 0.0
        private set

    var zoomFactor = 1.0

    // The current position in [0, 1] representing MIN/MAX

    var x = 0.0
    var y = 0.0

    override fun toString(): String {
        return "VirtualGrid([$rows,$columns],[$totalWidth,$totalHeight],[$clipWidth,$clipHeight],$zoomFactor)"
    }

    fun setClip(width: Double, height: Double) {
        clipWidth = width
        clipHeight = height
    }

    fun initGrid(newRows: Int, newColumns: Int) {
        if (newRows > rowHeights.size) {
            val oldRows = rowHeights.size
            rowHeights = rowHeights.copyOf(newRows)
            for (i in oldRows until newRows) {
                rowHeights[i] = minMax.minCellHeight
            }
        }
        if (newColumns > columnWidths.size) {
            val oldColumns = columnWidths.size
            columnWidths = columnWidths.copyOf(newColumns)
            for(i in oldColumns until newColumns) {
                columnWidths[i] = minMax.maxCellWidth
            }
        }
        rows = newRows
        columns = newColumns
        totalWidth = 0.0
        for (i in 0 until columns) {
            totalWidth += columnWidths[i]
        }
        totalHeight = 0.0
        for (i in 0 until rows) {
            totalHeight += rowHeights[i]
        }
    }
}