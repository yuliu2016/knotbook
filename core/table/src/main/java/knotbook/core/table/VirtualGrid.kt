package knotbook.core.table

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

    /**
     * Configuration limits for the grid
     */
    data class MinMax(
            val minCellHeight: Double = 18.0,
            val maxCellHeight: Double = 30.0,
            val minCellWidth: Double = 80.0,
            val maxCellWidth: Double = 400.0,
            val minZoomFactor: Double = 0.5,
            val maxZoomFactor: Double = 2.0
    )

    // Minimum and maximum bounds for the grid

    var minMax = MinMax()
        private set

    // The size of the grid to display

    var rows = 0
        private set
    var columns = 0
        private set

    // The width and heights of individual cells

    var colWidths = DoubleArray(0)
        private set
    var rowHeights = DoubleArray(0)
        private set

    // The position of grid lines

    var colPositions = DoubleArray(0)
        private set
    var rowPositions = DoubleArray(0)
        private set

    // The number of lines that need to show on the screen

    var virtualGridRows = 0
        private set
    var virtualGridCols = 0
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
        private set

    // The current position in [0, 1] representing MIN/MAX

    var x = 0.0
        private set
    var y = 0.0
        private set

    // The last position of the mouse
    var mouseX = 0.0
        private set
    var mouseY = 0.0
        private set

    // State markers
    var requiresRowLayout = false
        private set
    var requiresColLayout = false
        private set


    override fun toString(): String {
        return "VirtualGrid([$rows,$columns],[$totalWidth,$totalHeight],[$clipWidth,$clipHeight],$zoomFactor)"
    }

    /**
     * Set the grid size to ([newRows], [newColumns]]
     */
    fun initGrid(newRows: Int, newColumns: Int) {

        require(newRows > 0 && newColumns > 0) {
            "Dimensions cannot be less than 0"
        }

        if (newRows > rowHeights.size) {
            val oldRows = rowHeights.size
            rowHeights = rowHeights.copyOf(newRows)

            for (i in oldRows until newRows) {
                rowHeights[i] = minMax.minCellHeight
            }
        }

        if (newColumns > colWidths.size) {
            val oldColumns = colWidths.size
            colWidths = colWidths.copyOf(newColumns)

            for (i in oldColumns until newColumns) {
                colWidths[i] = minMax.maxCellWidth
            }
        }

        rows = newRows
        columns = newColumns

        totalWidth = 0.0
        for (i in 0 until columns) {
            totalWidth += colWidths[i]
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
            val effectiveClipWidth = computeEffectiveClipWidth()

            if (totalWidth > effectiveClipWidth) {
                x -= dx / (totalWidth - effectiveClipWidth)
                if (x < 0) x = 0.0
                if (x > 1) x = 1.0
            }
        } else {
            val effectiveClipHeight = computeEffectiveClipHeight()

            if (totalHeight > effectiveClipHeight) {
                y -= dy / (totalHeight - effectiveClipHeight)
                if (y < 0) y = 0.0
                if (y > 1) y = 1.0
            }
        }
    }

    /**
     * @return the effective clip width
     */
    fun computeEffectiveClipWidth(): Double {
        return clipWidth / zoomFactor
    }

    fun computeEffectiveClipHeight(): Double {
        return clipHeight / zoomFactor
    }

    /**
     * @return the set size of [rows] that need to be made virtual
     */
    fun computeVirtualRows(): Int {
        return (clipHeight / (minMax.minCellHeight * zoomFactor)).toInt() + 2
    }

    /**
     * @return the set size of [columns] that need to be made virtual
     */
    fun computeVirtualCols(): Int {
        return (clipHeight / (minMax.minCellHeight * zoomFactor)).toInt() + 2
    }

    /**
     * Set the clipping region to ([width], [height])
     */
    fun updateContentClip(width: Double, height: Double) {
        if (width == clipWidth && height == clipHeight) {
            return
        }

        require(width > minMax.minCellWidth && height > minMax.maxCellHeight) {
            "Clip dimension is set to less than the minimum cell dimension"
        }

        clipWidth = width
        clipHeight = height

        virtualGridRows = computeVirtualRows()
        if (virtualGridRows > rowPositions.size) {
            rowPositions = DoubleArray(virtualGridRows)
            updateRowState()
        }

        virtualGridCols = computeVirtualCols()
        if (virtualGridCols > colPositions.size) {
            colPositions = DoubleArray(virtualGridCols)
            updateColState()
        }
    }

    /**
     * Update the row state and mark for update
     */
    fun updateRowState() {
        doUpdateArrayState(virtualGridRows, rows, minMax.minCellHeight, rowHeights, rowPositions)
        markRowStateChanged()
    }

    /**
     * Update the column state and mark for update
     */
    fun updateColState() {
        doUpdateArrayState(virtualGridCols, columns, minMax.minCellWidth, colWidths, colPositions)
        markColStateChanged()
    }

    /**
     * Perform a virtual state update on either row or column
     */
    fun doUpdateArrayState(virtual: Int, actual: Int, min: Double, arr: DoubleArray, positions: DoubleArray) {
        check(positions.size >= virtual) {
            "Cannot update state - Position bound limited"
        }
        var start = 0.0
        for (j in 0 until virtual) {
            positions[j] = start
            start += if (j >= actual) min else arr[j]
        }
    }

    /**
     * Marks rows for relayout
     */
    fun markRowStateChanged() {
        requiresRowLayout = true
    }

    /**
     * Marks columns for relayout
     */
    fun markColStateChanged() {
        requiresColLayout = true
    }

    /**
     * Event callback if row layout is required
     */
    fun doIfRowStateChanged(action: () -> Unit) {
        if (requiresRowLayout) {
            action()
            requiresColLayout = false
        }
    }

    /**
     * Event callback if column is required
     */
    fun doIfColStateChanged(action: () -> Unit) {
        if (requiresColLayout) {
            action()
            requiresColLayout = false
        }
    }

    /**
     * Sets the mouse position
     */
    fun setMouse(x: Double, y: Double) {
        mouseX = x
        mouseY = y
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