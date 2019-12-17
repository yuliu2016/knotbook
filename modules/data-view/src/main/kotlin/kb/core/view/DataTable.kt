package kb.core.view

import javafx.scene.paint.Color
import kb.core.view.app.Singleton
import kb.service.api.array.TableArray
import kb.service.api.array.Tables
import kb.service.api.ui.RGB
import org.kordamp.ikonli.Ikon

@Suppress("MemberVisibilityCanBePrivate")
data class DataTable(
        val title: String,
        val array: TableArray,
        val icon: Ikon? = null,
        val iconColor: Color = Color.GRAY
) {
    val grid = array.toGrid()
    val referenceOrder = grid.rows.toList()
    private val sortColumns = ArrayList<SortColumn>()
    private val colourScales = ArrayList<ColorScale>()


    fun addSort(selectedColumns: Set<Int>, type: SortType) {
        selectedColumns.forEach {
            val sc = SortColumn(it, type)
            sortColumns.remove(sc)
            sortColumns.add(sc)
        }
        updateSort()
    }

    fun setSort(selectedColumns: Set<Int>, type: SortType) {
        sortColumns.clear()
        selectedColumns.forEach {
            val sc = SortColumn(it, type)
            sortColumns.add(sc)
        }
        updateSort()
    }

    fun clearSort() {
        sortColumns.clear()
        grid.rows.setAll(referenceOrder)
    }

    fun updateSort() {
        if (referenceOrder.isEmpty()) return
        val comparator = sortColumns.map {
            when (it.sortType) {
                SortType.Ascending -> Tables.ascendingComparator(array, it.index)
                SortType.Descending -> Tables.descendingComparator(array, it.index)
            }
        }.reduce { a, b -> a.then(b) }
        val order = (1 until referenceOrder.size).sortedWith(comparator)
        grid.rows.setAll(referenceOrder[0])
        grid.rows.addAll(order.map { referenceOrder[it] })
    }

    fun addColourScale(selectedColumns: Set<Int>, type: SortType, rgb: RGB) {
        selectedColumns.forEach {
            val cs = ColorScale(it, type, rgb)
            colourScales.remove(cs)
            colourScales.add(cs)
        }
        updateColourScale()
    }

    fun clearColourScale(selectedColumns: Set<Int>) {
        val rows = array.rows
        selectedColumns.forEach {
            val cs = ColorScale(it, SortType.Descending, PresetCS.green)
            colourScales.remove(cs)
            for (row in 0 until rows) {
                referenceOrder[row][it].style = null
            }
        }
    }

    fun updateColourScale() {
        val rows = array.rows
        val bg = if (Singleton.uiManager.isDarkTheme()) 0 else 255
        for (colourScale in colourScales) {
            val desc = colourScale.sortType == SortType.Descending
            val col = colourScale.index
            val values = (0 until rows).map { array[it, col] }

            var min = Double.MAX_VALUE
            var max = Double.MIN_VALUE
            for (v in values) {
                if (v.isFinite()) {
                    if (v < min) min = v
                    if (v > max) max = v
                }
            }

            for (row in 0 until rows) {
                val v = values[row]
                if (v.isInfinite() || v.isNaN()) continue
                val x = if (desc) (max - v) / (max - min) else (v - min) / (max - min)
                // Square the output so that the comparison is more obvious
                val y = if (desc) x * x else 1 - (1 - x) * (1 - x)
                referenceOrder[row][col].style = colourScale.rgb.blendStyle(y, bg)
            }
        }
    }
}