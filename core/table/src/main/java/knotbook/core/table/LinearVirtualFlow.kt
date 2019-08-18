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

class LinearVirtualFlow {

    // === MIN-MAX DIMENSIONS ===

    var minSize = 0.0
    var maxSize = 0.0

    // === TOTAL FINITE DIMENSIONS ===

    // The total number of sections to display

    var cells = 0
        private set

    // The size of individual cells

    var cellSizes = DoubleArray(0)

    // The position of lines separating the

    var positions = DoubleArray(0)

    // The total dimensions of the entire grid
    // Equal to the sum of the individual dimensions up to the specified size

    var totalSize = 0.0
        private set

    // === VIRTUAL DIMENSIONS ===

    // The number of lines that need to show on the screen

    var virtualCells = 0
        private set

    // The position of grid lines

    var virtualPositions = DoubleArray(0)

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
}