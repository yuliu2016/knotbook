package kb.core.view

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.stage.Screen
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.view.app.Singleton
import kb.service.api.array.TableArray
import kb.service.api.array.Tables
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem
import kb.service.api.ui.RGB
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetCell
import org.controlsfx.control.spreadsheet.SpreadsheetView


@Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode", "unused")
class DataView {

    val stage = Stage()
    val themeListener = InvalidationListener { updateTheme() }
    private var isFullScreen = false
    private var grid = GridBase(0, 0)

    init {
        stage.title = "KnotBook"
    }

    fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        stage.isFullScreen = isFullScreen
    }

    fun toggleStatusBar() {
        if (layout.bottom == null) layout.bottom = statusBar
        else layout.bottom = null
    }

    fun updateTheme() {
        val theme = Singleton.uiManager.themeProperty.get()
        layout.stylesheets.setAll("/knotbook.css", theme.viewStyle)
        updateCS()
    }

    val calculations = Label()

    private val statusBar = hbox {
        align(Pos.CENTER_LEFT)
        padding = Insets(0.0, 12.0, 0.0, 12.0)
        prefHeight = 22.0
        styleClass("status-bar")
        spacing = 12.0
        add(calculations)
        hspace()
    }

    val spreadsheet = SpreadsheetView(grid).apply {
        selectionModel.selectedCells.addListener(InvalidationListener { onSelectionChanged() })
        columns.forEach { it.setPrefWidth(75.0) }
        zoomFactorProperty().addListener(InvalidationListener { zoomText.value = "${(zoomFactor * 100).toInt()}%" })
        contextMenu = null
        isEditable = false
    }

    val layout = borderPane {
        prefWidth = 720.0
        prefHeight = 480.0
        center = spreadsheet
        bottom = statusBar
    }

    val scene = Scene(layout)

    var showing = false

    fun addStatus(prop: StringProperty) {
        statusBar.add(label {
            textProperty().bind(prop)
        })
    }

    fun selectAll() {
        spreadsheet.selectionModel.selectAll()
    }

    fun selectNone() {
        spreadsheet.selectionModel.clearSelection()
    }

    val zoomText = SimpleStringProperty("100%")
    val themeText = SimpleStringProperty("Light")
    val selectionText = SimpleStringProperty("None")

    fun show() {
        if (showing) throw IllegalStateException("DataView is already shown")
        showing = true

        val area = Screen.getPrimary().visualBounds
        layout.prefWidth = area.width / 2.0
        layout.prefHeight = area.height / 2.0 + 32.0

        updateTheme()
        themeText.bind(Singleton.uiManager.themeProperty.asString())
        addStatus(selectionText)
        addStatus(zoomText)
        addStatus(themeText)
        addStatus(Singleton.uiManager.memoryUsed)

        Singleton.uiManager.themeProperty.addListener(themeListener)
        Singleton.uiManager.commandManager.forEachShortcut { shortcut, key ->
            scene.accelerators[shortcut] = Runnable { Singleton.uiManager.commandManager.invokeCommand(key) }
        }
        stage.fullScreenExitHint = "Press F11 to Exit Full Screen"
        stage.icons.add(Singleton.appIcon)
        stage.scene = scene
        stage.focusedProperty().addListener { _, _, focused ->
            val m = Singleton.uiManager
            if (focused) {
                m.view = this
            } else if (m.view === this) {
                m.view = null
            }
        }
        stage.setOnCloseRequest { Singleton.uiManager.themeProperty.removeListener(themeListener) }
        stage.show()
    }

    var array: TableArray? = null

    private val sortColumns = ArrayList<SortColumn>()
    private val colourScales = ArrayList<ColorScale>()
    private var referenceOrder: List<ObservableList<SpreadsheetCell>> = ArrayList()

    fun setData(title: String, data: TableArray) {
        stage.title = title
        array = data
        grid = data.toGrid()
        this.spreadsheet.grid = grid
        referenceOrder = grid.rows.toList()
        spreadsheet.fixedRows.setAll(0)
    }

    fun getSelectedColumns(): Set<Int> {
        return spreadsheet.selectionModel.selectedCells.mapTo(HashSet()) { it.column }
    }

    fun addSort(type: SortType) {
        getSelectedColumns().forEach {
            val sc = SortColumn(it, type)
            sortColumns.remove(sc)
            sortColumns.add(sc)
        }
        updateSort()
    }

    fun setSort(type: SortType) {
        sortColumns.clear()
        getSelectedColumns().forEach {
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
        val array = array ?: return
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

    fun addCS(type: SortType, rgb: RGB) {
        getSelectedColumns().forEach {
            val cs = ColorScale(it, type, rgb)
            colourScales.remove(cs)
            colourScales.add(cs)
        }
        updateCS()
    }

    fun clearCS() {
        val array = array ?: return
        val rows = array.rows
        getSelectedColumns().forEach {
            val cs = ColorScale(it, SortType.Descending, PresetCS.green)
            colourScales.remove(cs)
            for (row in 0 until rows) {
                referenceOrder[row][it].style = null
            }
        }
    }

    fun updateCS() {
        val array = array ?: return
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


    private inline fun copyWithMinMax(block: (minRow: Int, maxRow: Int, minCol: Int, maxCol: Int) -> String) {
        val se = spreadsheet.getSelection()
        val content = ClipboardContent()
        content.putString(block(se.minRow, se.maxRow, se.minCol, se.maxCol))
        Clipboard.getSystemClipboard().setContent(content)
    }

    fun copyDelimited(delimiter: Char) {
        copyWithMinMax { minRow, maxRow, minCol, maxCol ->
            val builder = StringBuilder()
            for (i in minRow..maxRow) {
                for (j in minCol until maxCol) {
                    val it = grid.rows[i][j].item
                    if (it != null) builder.append(it)
                    builder.append(delimiter)
                }
                val it = grid.rows[i][maxCol].item
                if (it != null) builder.append(it)
                builder.append("\n")
            }
            builder.toString()
        }
    }

    fun saveCSV() {
        val array = array ?: return
        val fp = Singleton.getSavePath(this, "csv")
        if (fp != null) {
            Tables.toCSV(array, fp.outputStream(), ',')
        }
    }

    fun saveZip() {
        val array = array ?: return
        val fp = Singleton.getSavePath(this, "kbt")
        if (fp != null) {
            Tables.toZip(array, fp.outputStream())
        }
    }

    fun startFind() {
        val ob = OptionBar()
        ob.hint = "Enter something to search for"

        ob.setOnEnterPressed {
            ob.isShowing = false
        }

        ob.textProperty().addListener(InvalidationListener {
            val t = ob.text.trim()
            if (t.isEmpty()) {
                ob.items.clear()
                return@InvalidationListener
            }
            try {
                val num = t.toDouble()
                val array = array ?: return@InvalidationListener
                val rows = array.rows
                var count = 0
                for (i in 0 until rows) {
                    for (j in 0 until array.cols) {
                        if (array[i, j] == num) {
                            count++
                        }
                    }
                }
                ob.items.setAll(
                        OptionItem("Results in Table", null, "$count Found", null, null)
                )
            } catch (e: Exception) {
            }
        })


        Singleton.uiManager.showOptionBar(ob)
    }

    private fun onSelectionChanged() {
        selectionText.value = getRangeText()
        val a = spreadsheet.selectionModel.selectedCells
        if (a.size < 2) {
            calculations.text = ""
            return
        }
        val array = array ?: return
        var count = 0
        var sum = 0.0
        var min = Double.MAX_VALUE
        var max = Double.MIN_VALUE
        for (pos in a) {
            val num = array[pos.row, pos.column]
            if (num.isFinite()) {
                count++
                sum += num
                if (num < min) min = num
                if (num > max) max = num
            }
        }
        if (count == 0) {
            calculations.text = ""
            return
        }
        val average = (sum / count).toFloat() // make it a shorter string
        calculations.text = "Sum: $sum    Count: $count    Average: $average    Min: $min    Max: $max"
    }

    private fun getRangeText(): String {
        val a = spreadsheet.selectionModel.selectedCells
        if (a.isEmpty()) return "None"
        val rows = a.map { it.row }
        val cols = a.map { it.column }
        val w = rows.min()!! + 1
        val x = rows.max()!! + 1
        val y = Tables.columnIndexToString(cols.min()!!)
        val z = Tables.columnIndexToString(cols.max()!!)
        return if (a.size == 1) "$y$w" else "$y$w:$z$x"
    }
}