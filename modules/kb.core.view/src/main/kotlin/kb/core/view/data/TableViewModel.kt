package kb.core.view.data

import javafx.collections.ObservableList
import javafx.scene.control.ContextMenu
import javafx.scene.input.KeyCode
import kb.core.fx.*
import kb.core.icon.icon
import krangl.*
import org.controlsfx.control.spreadsheet.Grid
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetCell
import org.controlsfx.control.spreadsheet.SpreadsheetCellType
import org.kordamp.ikonli.materialdesign.MaterialDesign
import java.text.DecimalFormat

@Suppress("UsePropertyAccessSyntax", "unused", "UNUSED_PARAMETER")
class TableViewModel(private val df: DataFrame) {

    fun getDataFrame(): DataFrame {
        return df
    }

    private val decimalFormat = DecimalFormat("####0.000")

    private val sortColumns: MutableList<SortColumn> = mutableListOf()

    private val colourScales: MutableList<ColorScale> = mutableListOf()

    private val grid: Grid = toGrid()

    private fun toGrid(): Grid {
        val grid = GridBase(df.nrow, df.ncol)
        grid.rows.addAll(df.rows.mapIndexed { i, row ->
            row.values.mapIndexed { j, value ->
                if (value is Double) {
                    SpreadsheetCellType.STRING.createCell(i, j,
                            1, 1, decimalFormat.format(value))
                } else {
                    SpreadsheetCellType.STRING.createCell(i, j,
                            1, 1, value?.toString() ?: "")
                }
            }.observable()
        })
        grid.columnHeaders.addAll(df.cols.map { it.name })
        return grid
    }

    private var referenceOrder: List<ObservableList<SpreadsheetCell>> = grid.rows.toList()

    private val headers = df.cols.map { it.name }

    fun getGrid(): Grid {
        return grid
    }

    fun ContextMenu.updateMenu(): ContextMenu = modify {
        submenu {
            name("Sort")
            icon(MaterialDesign.MDI_SORT, 18)
            modify {
                item {
                    name("Set Ascending")
                    shortcut(KeyCode.EQUALS)
                    action { setSort(SortType.Ascending) }
                }
                item {
                    name("Set Descending")
//                    accelerator = Combo(KeyCode.MINUS)
                    action { setSort(SortType.Descending) }
                }
                item {
                    name("Add Ascending")
                    action { addSort(SortType.Ascending) }
                }
                item {
                    name("Add Descending")
                    action { addSort(SortType.Descending) }
                }
                item {
                    name("Clear Selected Columns")
                    action { clearSelectedSort() }
                }
                item {
                    name("Clear All")
//                    accelerator = Combo(KeyCode.DIGIT0)
                    action { clearSort() }
                }
            }
        }
        submenu {
            name("Copy")
//            icon(FontAwesomeRegular.COPY, 18)
            modify {
                item {
                    name("Tab-Delimited With Headers")
//                    accelerator = Combo(KeyCode.C, KeyCombination.SHORTCUT_DOWN)
                    action { tabDelimitedHeaders() }
                }
                item {
                    name("Tab-Delimited")
                    action { tabDelimited() }
                }
                item {
                    name("Comma-Delimited With Headers")
                    setOnAction { commaDelimitedHeaders() }
                }
                item {
                    name("Comma-Delimited")
                    action { commaDelimited() }
                }
                item {
                    name("Python Dict of List Cols")
                    action { dictOfListColumns() }
                }
            }
        }
        submenu {
            name("Format")
//            icon(FontAwesomeSolid.SUN, 18)
            modify {
                item {
                    name("Green Scale Ascending")
                    action { addColourScale(SortType.Ascending, 96, 192, 144) }
                }
                item {
                    name("Green Scale Descending")
                    action { addColourScale(SortType.Descending, 96, 192, 144) }
                }
                item {
                    name("Orange Scale Ascending")
                    action { addColourScale(SortType.Ascending, 255, 144, 0) }
                }
                item {
                    name("Orange Scale Descending")
                    action { addColourScale(SortType.Descending, 255, 144, 0) }
                }
                item {
                    name("Red Scale Ascending")
                    action { addColourScale(SortType.Ascending, 255, 108, 108) }
                }
                item {
                    name("Red Scale Descending")
                    action { addColourScale(SortType.Descending, 255, 108, 108) }
                }
                item {
                    name("Blue Scale Ascending")
                    action { addColourScale(SortType.Ascending, 110, 170, 256) }
                }
                item {
                    name("Blue Scale Descending")
                    action { addColourScale(SortType.Descending, 110, 170, 256) }
                }
                item {
                    name("Clear Formatting")
                    action { clearColourScale() }
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun addColourScale(sortType: SortType, r: Int, g: Int, b: Int) {
//        val selection = getSelection()
//        if (selection.cols.isNotEmpty()) {
//            selection.cols.forEach {
//                val col = ColorScale(it, sortType, r, g, b)
//                colourScales.remove(col)
//                colourScales.add(col)
//            }
//            applyColourScale()
//        }
    }

    private fun applyColourScale() {
        for (colorScale in colourScales) {
            val col = df.cols[colorScale.index]
            val comparator = when (colorScale.sortType) {
                SortType.Ascending -> col.descendingComparator().reversed()
                SortType.Descending -> col.descendingComparator()
            }
            val indices = (0 until df.nrow).sortedWith(comparator)

            val values = when (col) {
                is DoubleCol -> col.values.toList()
                is IntCol -> col.values.map { it?.toDouble() }
                is LongCol -> col.values.map { it?.toDouble() }
                is StringCol -> (0 until df.nrow).map { indices.indexOf(it).toDouble() }
                else -> listOf()
            }

            var min = 0.0
            var max = 0.0

            when (colorScale.sortType) {
                SortType.Ascending -> {
                    min = values[indices.first()] ?: 0.0
                    var i = indices.size - 1
                    while (i > 0 && values[indices[i]] == null) {
                        i--
                    }
                    max = values[indices[i]]!!
                }
                SortType.Descending -> {
                    max = values[indices.last()] ?: 0.0
                    var i = 0
                    while (i < indices.size && values[indices[i]] == null) {
                        i++
                    }
                    min = values[indices[i]]!!
                }
            }

            val r = colorScale.r
            val g = colorScale.g
            val b = colorScale.b

            for (row in 0 until df.nrow) {
                val n = values[row] ?: 0.0
                val x = (n - min) / (max - min)
                val a = ((x * 100).toInt() / 100.0)
                referenceOrder[row][colorScale.index].apply {
                    styleClass.add("reload-hot-fix")
                    style = "-fx-background-color: rgba($r, $g, $b, $a)"
                }
            }
        }
    }

    private fun clearColourScale() {
        for (colorScale in colourScales) {
            for (row in 0 until df.nrow) {
                referenceOrder[row][colorScale.index].apply {
                    styleClass.remove("reload-hot-fix")
                    style = ""
                }
            }
        }
        colourScales.clear()
    }

    private fun setSort(type: SortType) {
        sortColumns.clear()
        addSort(type)
    }

    private fun addSort(type: SortType) {
//        val selection = getSelection()
//        if (selection.cols.isNotEmpty()) {
//            selection.cols.forEach {
//                val col = SortColumn(type, it)
//                sortColumns.remove(col)
//                sortColumns.add(col)
//            }
//            applySort()
//        }
    }

    private fun clearSort() {
        sortColumns.clear()
        grid.columnHeaders.setAll(headers)
        grid.rows.setAll(referenceOrder)
//        view.sortList.items.clear()
    }

    private fun clearSelectedSort() {
//        val selection = getSelection()
//        if (selection.cols.isNotEmpty()) {
//            sortColumns.removeAll { it.index in selection.cols }
//            applySort()
//        }
    }

    private fun applySort() {
        val totalOrderComparator = sortColumns.map {
            when (it.sortType) {
                SortType.Ascending -> df.cols[it.index].ascendingComparator()
                SortType.Descending -> df.cols[it.index].descendingComparator()
            }
        }.reduce { a, b -> a.then(b) }
        val sortedOrder = (0 until df.nrow).sortedWith(totalOrderComparator)
        grid.rows.setAll(sortedOrder.map { referenceOrder[it] })
        grid.columnHeaders.setAll(headers)
        sortColumns.forEachIndexed { i, sortColumn ->
            grid.columnHeaders[sortColumn.index] += when (sortColumn.sortType) {
                SortType.Ascending -> " " + "\u25B4".repeat(i + 1)
                SortType.Descending -> " " + "\u25be".repeat(i + 1)
            }
        }
//        view.sortList.items.setAll(sortColumns.map {
//            df.cols[it.index].name + "  >>  " + it.sortType.name
//        })
    }

    private inline fun copyWithMinMax(block: (minRow: Int, maxRow: Int, minCol: Int, maxCol: Int) -> String) {
//        val se = getSelection()
//        val content = ClipboardContent()
//        content.putString(block(se.minRow, se.maxRow, se.minCol, se.maxCol))
//        Clipboard.getSystemClipboard().setContent(content)
    }

    private fun copyDelimited(delimiter: Char, headers: Boolean) {
        copyWithMinMax { minRow, maxRow, minCol, maxCol ->
            val builder = StringBuilder()
            if (headers && grid.columnHeaders.isNotEmpty() && minCol != maxCol) {
                for (col in minCol until maxCol) {
                    builder.append(grid.columnHeaders[col])
                    builder.append(delimiter)
                }
                builder.append(grid.columnHeaders[maxCol])
                builder.append("\n")
            }
            for (i in minRow..maxRow) {
                for (j in minCol until maxCol) {
                    builder.append(grid.rows[i][j].item)
                    builder.append(delimiter)
                }
                builder.append(grid.rows[i][maxCol].item)
                builder.append("\n")
            }
            builder.toString()
        }
    }

    private fun tabDelimitedHeaders() {
        copyDelimited('\t', true)
    }

    private fun tabDelimited() {
        copyDelimited('\t', false)
    }

    private fun commaDelimitedHeaders() {
        copyDelimited(',', true)
    }

    private fun commaDelimited() {
        copyDelimited(',', false)
    }

    private fun dictOfListColumns() {
        copyWithMinMax { minRow, maxRow, minCol, maxCol ->
            val builder = StringBuilder()
            builder.append('{')
            if (df.cols.isNotEmpty() && minCol != maxCol) {
                for (col in minCol..maxCol) {
                    val column = df.cols[col]
                    builder.append("\n\"")
                            .append(column.name)
                            .append("\": [")
                    if (column is NumberCol || column is DoubleCol) {
                        for (row in minRow..maxRow) {
                            val item = column[row]
                            if (item == null) builder.append("None")
                            else builder.append(item)
                            if (row != maxRow) {
                                builder.append(", ")
                            }
                        }
                    } else {
                        for (row in minRow..maxRow) {
                            val item = column[row]
                            if (item == null) builder.append("None")
                            else builder.append("\"").append(item).append("\"")
                            if (row != maxRow) {
                                builder.append(", ")
                            }
                        }
                    }
                    builder.append("]")
                    if (col != maxCol) {
                        builder.append(",")
                    }
                }
                builder.append("\n")
            }
            builder.append('}')
            builder.toString()
        }
    }
}