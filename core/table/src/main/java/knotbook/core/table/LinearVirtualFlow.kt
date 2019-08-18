package knotbook.core.table

/**
 * Pure implementation of VirtualFlow that is independent of cells
 * or scroll bars; those can be handled in another class
 *
 * [LinearVirtualFlow] represents the view in 1D. 2 instances of this
 * class can be used to construct a virtual flow mechanism for an
 * actual table/grid
 *
 * The class also includes some fine tuned calculations for
 * JavaFX layout.
 *
 * Usage of this class requires calling the right methods after
 * changing parameters, because it's too complex to listen for changes.
 */

@Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate", "unused")
class LinearVirtualFlow {

    // === MIN-MAX DIMENSIONS ===

    var minSize = 0.0

    // === TOTAL FINITE DIMENSIONS ===

    // The total number of sections to display

    var cellCount = 0
        private set

    // The size of individual cells

    var cellSizes = DoubleArray(0)

    // The position of lines separating the

    var cellPos = DoubleArray(0)

    // The total dimensions of the entire grid
    // Equal to the sum of the individual dimensions up to the specified size

    var totalSize = 0.0
        private set

    // === VIRTUAL DIMENSIONS ===

    // The number of lines that need to show on the screen

    var virtualCellCount = 0
        private set

    // The position of grid lines

    var virtualCellPos = DoubleArray(0)

    // The size of the virtual box

    var clipSize = 0.0
        private set

    // === VIEW CONFIGURATION ===

    // The zoom factor -- Everything else is invariant to the zoom factor
    // Must apply it in every calculation to avoid changing other parameters

    var zoomFactor = 1.0
        private set

    // The current position in [0, 1] representing MIN/MAX

    var scroll = 0.0
        private set

    // === COMPUTE STATE/MARKERS ===

    // State markers
    var requiresLayout = false
        private set

    /**
     * Set the content size to [newCount]
     */
    fun setCellCount(newCount: Int) {

        require(newCount > 0) {
            "Dimensions cannot be less than 0"
        }

        if (newCount > cellSizes.size) {

            val oldCount = cellSizes.size

            cellSizes = cellSizes.copyOf(newCount)

            for (i in oldCount until newCount) {
                cellSizes[i] = minSize
            }

            cellPos = sumIntoOrExpand(cellSizes, cellPos)
        }

        cellCount = newCount

        totalSize = 0.0

        for (i in 0 until cellCount) {
            totalSize += cellSizes[i]
        }
    }

    /**
     * Sum an array of values into another array,
     * such that the i-th item in the resulting array is the
     * sum of all values between the index of [0, i] in the input
     * array.
     *
     * @param sumCache accepts a cached array to reuse memory. This
     * array is expanded as needed to fit the size of [values]
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

    /**
     * Marks for relayout
     */
    fun markStateChanged() {
        requiresLayout = true
    }


    /**
     * Constrains [scroll] between [0, 1] and updates the state
     */
    fun constrainScroll() {
        if (scroll < 0) {
            scroll = 0.0
        }
        if (scroll > 1) {
            scroll = 1.0
        }
        updateCellState()
    }

    /**
     * Scrolls to a specific point between [0, 1]
     */
    fun scrollTo(newScroll: Double) {
        if (newScroll != scroll) {
            scroll = newScroll
            constrainScroll()
        }
    }

    /**
     * @return the effective clip size
     */
    fun computeEffectiveClipSize(): Double {
        return clipSize / zoomFactor
    }

    /**
     * Scroll the grid by ([ds])
     *
     * Should be called from a scroll event listener
     */
    fun scrollBy(ds: Double) {

        val effectiveClipSize = computeEffectiveClipSize()

        if (kotlin.math.abs(ds) > 0 && totalSize > effectiveClipSize) {

            scroll -= ds / (totalSize - effectiveClipSize)

            constrainScroll()
        }
    }

    /**
     * @return the set size of [cellCount] that need to be made virtual
     */
    fun computeVirtualCells(): Int {
        return (clipSize / (minSize * zoomFactor)).toInt() + 2
    }

    /**
     * Update the row state and mark for update
     */
    fun updateCellState() {

        check(virtualCellPos.size >= virtualCellCount) {
            "Cannot update row state - Position bound limited"
        }

        var start = -scroll * 300

        for (j in 0 until virtualCellCount) {

            virtualCellPos[j] = start

            start += if (j >= cellCount) minSize else cellSizes[j]
        }

        markStateChanged()
    }


    /**
     * Set the clipping region to [size]
     *
     * This method only applies when the clip dimensions have changed
     */
    fun updateContentClip(size: Double) {
        println(size)
        if (size == clipSize) {
            return
        }

        require(size >= minSize) {
            "Clip dimension is set to less than the minimum cell dimension"
        }

        clipSize = size
        virtualCellCount = computeVirtualCells()

        if (virtualCellCount > virtualCellPos.size) {
            virtualCellPos = DoubleArray(virtualCellCount)
            updateCellState()
        }
    }


    /**
     * Use the result of [computeEffectiveClipSize] to calculate
     * thumb size, defined as the ratio of the clip size to the
     * total size of the virtual view
     *
     * Limits result to < 1.0 because a division by 0 is possible
     * when [setCellCount] is not called before this method
     *
     * @return the thumb size for the scroll bar in [0, 1]
     */
    fun thumbSize(): Double {
        val effectiveClipSize = computeEffectiveClipSize()
        if (totalSize <= effectiveClipSize) {
            return 1.0
        }
        return effectiveClipSize / totalSize
    }

    /**
     * Event callback if column is required
     */
    fun doIfStateChanged(action: () -> Unit) {
        if (requiresLayout) {
            action()
            requiresLayout = false
        }
    }

    override fun toString(): String {
        return "LVF(cnt=$cellCount, tot=$totalSize, vce=$virtualCellCount, " +
                "cli=$clipSize, zoo=$zoomFactor, scr=$scroll)"
    }
}