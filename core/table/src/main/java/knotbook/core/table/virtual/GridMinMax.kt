package knotbook.core.table.virtual

data class GridMinMax(
        val minCellHeight: Double = 18.0,
        val maxCellHeight: Double = 30.0,
        val minCellWidth: Double = 80.0,
        val maxCellWidth: Double = 400.0,
        val minZoomFactor: Double = 0.5,
        val maxZoomFactor: Double = 2.0
)