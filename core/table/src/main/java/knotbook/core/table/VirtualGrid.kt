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
 * changing parameters, because it's too complex to listen for changes.
 */

@Suppress("unused", "MemberVisibilityCanBePrivate")
class VirtualGrid {

    // === TOTAL FINITE DIMENSIONS ===

    // The size of the grid to display

    var cols = 0
        private set
    var rows = 0
        private set

    // The width and heights of individual cells

    var colWidths = DoubleArray(0)
    var rowHeights = DoubleArray(0)

    // The position of grid lines -- synced with widths and heights

    var colPos = DoubleArray(0)
    var rowPos = DoubleArray(0)

    // The total dimensions of the entire grid
    // Equal to the sum of the individual dimensions up to the specified size

    var totalWidth = 0.0
        private set
    var totalHeight = 0.0
        private set

    // === VIRTUAL DIMENSIONS ===

    // The number of lines that need to show on the screen

    var virtualCols = 0
        private set
    var virtualRows = 0
        private set

    // The position of grid lines

    var virtualColPos = DoubleArray(0)
    var virtualRowPos = DoubleArray(0)

    // The width and height of the virtual box

    var clipWidth = 0.0
        private set
    var clipHeight = 0.0
        private set

    // === VIEW CONFIGURATION ===

    // The zoom factor -- Everything else is invariant to the zoom factor
    // Must apply it in every calculation to avoid changing other parameters

    var zoomFactor = 1.0
        private set

    // The current position in [0, 1] representing MIN/MAX

    var scrollX = 0.0
        private set
    var scrollY = 0.0
        private set

    // === COMPUTE STATE/MARKERS ===

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

    // === GRID POLICY ===

    var policy = Policy()
        private set

    /**
     * Set the grid size to ([newRows], [newColumns]]
     */
    fun initGrid(newColumns: Int, newRows: Int) {

        require(newColumns > 0 && newRows > 0) {
            "Dimensions cannot be less than 0"
        }

        if (newColumns > colWidths.size) {

            val oldColumns = colWidths.size

            colWidths = colWidths.copyOf(newColumns)

            for (i in oldColumns until newColumns) {
                colWidths[i] = policy.minCellWidth
            }

            sumIntoOrExpand(colWidths, colPos)
        }

        if (newRows > rowHeights.size) {

            val oldRows = rowHeights.size

            rowHeights = rowHeights.copyOf(newRows)

            for (i in oldRows until newRows) {
                rowHeights[i] = policy.minCellHeight
            }

            sumIntoOrExpand(rowHeights, rowPos)
        }

        cols = newColumns
        rows = newRows

        totalWidth = 0.0

        for (i in 0 until cols) {
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
     * Should be called from a scroll event listener
     */
    fun scrollBy(dx: Double, dy: Double) {

        val effectiveClipWidth = computeEffectiveClipWidth()

        if (kotlin.math.abs(dx) > 0 && totalWidth > effectiveClipWidth) {

            scrollX -= dx / (totalWidth - effectiveClipWidth)

            constrainScrollX()
        }

        val effectiveClipHeight = computeEffectiveClipHeight()

        if (kotlin.math.abs(dy) > 0 && totalHeight > effectiveClipHeight) {

            scrollY -= dy / (totalHeight - effectiveClipHeight)

            constrainScrollY()
        }
    }

    /**
     * Constrains [scrollX] between [0, 1] and updates the state
     */
    fun constrainScrollX() {
        if (scrollX < 0) {
            scrollX = 0.0
        }
        if (scrollX > 1) {
            scrollX = 1.0
        }
        updateColState()
    }

    /**
     * Constrains [scrollY] between [0, 1]
     */
    fun constrainScrollY() {
        if (scrollY < 0) {
            scrollY = 0.0
        }

        if (scrollY > 1) {
            scrollY = 1.0
        }
        updateRowState()
    }

    /**
     * Scrolls to a specific point in the x direction
     */
    fun scrollToX(x: Double) {
        if (x != scrollX) {
            scrollX = x
            constrainScrollX()
        }
    }

    /**
     * Scrolls to a specific point in the y direction
     */
    fun scrollToY(y: Double) {
        if (y != scrollY) {
            scrollY = y
            constrainScrollY()
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
        return (clipHeight / (policy.minCellHeight * zoomFactor)).toInt() + 2
    }

    /**
     * @return the set size of [cols] that need to be made virtual
     */
    fun computeVirtualCols(): Int {
        return (clipHeight / (policy.minCellHeight * zoomFactor)).toInt() + 2
    }

    /**
     * Set the clipping region to ([width], [height])
     *
     * This method only applies when the clip dimensions have changed
     */
    fun updateContentClip(width: Double, height: Double) {
        if (width == clipWidth && height == clipHeight) {
            return
        }

        require(width >= policy.minCellWidth && height >= policy.minCellHeight) {
            "Clip dimension is set to less than the minimum cell dimension"
        }

        clipWidth = width
        clipHeight = height

        virtualRows = computeVirtualRows()

        if (virtualRows > virtualRowPos.size) {
            virtualRowPos = DoubleArray(virtualRows)
            updateRowState()
        }

        virtualCols = computeVirtualCols()

        if (virtualCols > virtualColPos.size) {
            virtualColPos = DoubleArray(virtualCols)
            updateColState()
        }
    }

    /**
     * Update the row state and mark for update
     */
    fun updateRowState() {

        check(virtualRowPos.size >= virtualRows) {
            "Cannot update row state - Position bound limited"
        }

        var start = -scrollY * 300

        for (j in 0 until virtualRows) {

            virtualRowPos[j] = start

            start += if (j >= rows) policy.minCellHeight else rowHeights[j]
        }

        markRowStateChanged()
    }

    /**
     * Update the column state and mark for update
     */
    fun updateColState() {

        check(virtualColPos.size >= virtualCols) {
            "Cannot update column state - Position bound limited"
        }

        var start = -scrollX * 300

        for (j in 0 until virtualCols) {

            virtualColPos[j] = start

            start += if (j >= cols) policy.minCellWidth else colWidths[j]
        }

        markColStateChanged()
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
            requiresRowLayout = false
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
     * Sets the mouse position.
     * This is used in conjunction with zooming -- it allows zooming
     * from a specific point by adjusting [scrollX] and [scrollY]
     *
     * This should be called from a mouse event listener
     */
    fun setMouse(x: Double, y: Double) {
        mouseX = x
        mouseY = y
    }

    /**
     * Use the result of [computeEffectiveClipWidth] to calculate
     * thumb size, defined as the ratio of the clip width to the
     * total width of the virtual view
     *
     * Limits result to < 1.0 because a division by 0 is possible
     * when [initGrid] is not called before this method
     *
     * @return the thumb size for the horizontal scroll bar in [0, 1]
     */
    fun horizontalThumbSize(): Double {
        val effectiveClipWidth = computeEffectiveClipWidth()
        if (totalWidth <= effectiveClipWidth) {
            return 1.0
        }
        return effectiveClipWidth / totalWidth
    }

    /**
     * Use the result of [computeEffectiveClipHeight] to calculate
     * thumb size, defined as the ratio of the clip height to the
     * total height of the virtual view
     *
     * Limits result to < 1.0 because a division by 0 is possible
     * when [initGrid] is not called before this method
     *
     * @return the thumb size for the vertical scroll bar in [0, 1]
     */
    fun verticalThumbSize(): Double {
        val effectiveClipHeight = computeEffectiveClipHeight()
        if (totalHeight <= effectiveClipHeight) {
            return 1.0
        }
        return effectiveClipHeight / totalHeight
    }

    /**
     * Sum an array of values into another array,
     * such that the i-th item in the resulting array is the
     * sum of all values between the index of [0, i] in the input
     * array.
     *
     * @param sumCache accepts a cached array to reuse memory. This
     * is used for [rowPos] and [colPos]. This array is expanded
     * as needed to fit the size of [values]
     *
     * @return the sum array
     */
    fun sumIntoOrExpand(values: DoubleArray, sumCache: DoubleArray): DoubleArray {
        val sum = if (values.size <= sumCache.size) {
            sumCache
        } else {
            DoubleArray(values.size)
        }

        var accumulator = 0.0

        for (i in 0 until values.size) {
            accumulator += values[i]
            sum[i] = accumulator
        }

        return sum
    }


    override fun toString(): String {
        return """VirtualGrid(
 dim   = [row=$rows, col=$cols],
 total = [w=$totalWidth, h=$totalHeight],
 clip  = [w=$clipWidth, h=$clipHeight],
 zoom  = [$zoomFactor],
 mouse = [x=$mouseX, y=$mouseY,
 scr   = [x=$scrollX, y=$scrollY],
 vir   = [row=$virtualRows, col=$virtualCols]
)"""
    }

    /**
     * Configuration limits for the grid
     */
    data class Policy(

            val minCellWidth: Double = 80.0,

            val maxCellWidth: Double = 400.0,

            val minCellHeight: Double = 18.0,

            val maxCellHeight: Double = 30.0,

            val minZoomFactor: Double = 0.5,

            val maxZoomFactor: Double = 2.0
    )
}