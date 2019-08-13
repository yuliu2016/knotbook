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
 * JavaFX layout.
 *
 * Usage of this class requires calling the right methods after
 * changing parameters, because it's too complex to listen for changes
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

    // The zoom factor -- Everything else is invariant to the zoom factor
    // Must apply it in every calculation to avoid changing other parameters

    var zoomFactor = 1.0

    // The current position in [0, 1] representing MIN/MAX

    var x = 0.0
    var y = 0.0

    override fun toString(): String {
        return "VirtualGrid([$rows,$columns],[$totalWidth,$totalHeight],[$clipWidth,$clipHeight],$zoomFactor)"
    }

    /**
     * Set the clipping region to ([width], [height])
     */
    fun setClip(width: Double, height: Double) {
        check(width > minMax.minCellWidth)
        check(height > minMax.maxCellHeight)
        clipWidth = width
        clipHeight = height
    }

    /**
     * Set the grid size to ([newRows], [newColumns]]
     */
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
            for (i in oldColumns until newColumns) {
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

    /**
     * Scroll the grid by ([dx], [dy])
     *
     * We make sure to only scroll in one direction
     */
    fun scroll(dx: Double, dy: Double) {
        if (dx > dy) {
            val effectiveClipWidth = clipWidth / zoomFactor
            if (totalWidth > effectiveClipWidth) {
                x -= dx / (totalWidth - effectiveClipWidth)
                if (x < 0) x = 0.0
                if (x > 1) x = 1.0
            }
        } else {
            val effectiveClipHeight = clipHeight / zoomFactor
            if (totalHeight > effectiveClipHeight) {
                y -= dy / (totalHeight - effectiveClipHeight)
                if (y < 0) y = 0.0
                if (y > 1) y = 1.0
            }
        }
    }

    /**
     * @return the set size of [rows] that need to be made virtual
     */
    fun virtualGridRows(): Int {
        return (clipHeight / (minMax.minCellHeight * zoomFactor)).toInt() + 2
    }

    /**
     * @return the set size of [columns] that need to be made virtual
     */
    fun virtualGridCols(): Int {
        return (clipHeight / (minMax.minCellHeight * zoomFactor)).toInt() + 2
    }

    /**
     * @return the thumb size for the horizontal scroll bar
     */
    fun horizontalThumbSize(): Double {
        return (totalWidth - clipWidth / zoomFactor) / totalWidth
    }

    /**
     * @return the thumb size for the horizontal scroll bar
     */
    fun verticalThumbSize(): Double {
        return (totalHeight - clipHeight / zoomFactor) / totalHeight
    }
}