package kb.core.view

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.stage.Screen
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.core.view.app.Singleton
import kb.service.api.array.Tables
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem
import kb.service.api.ui.RGB
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetView
import org.kordamp.ikonli.materialdesign.MaterialDesign


@Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode", "unused")
class DataView {

    val stage = Stage()
    var showing = false
    val themeListener = InvalidationListener { updateTheme() }
    private var isFullScreen = false
    val calculations = Label()
    val tables = mutableListOf<DataTable>()
    var activeTable: DataTable? = null

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
        activeTable?.updateColourScale()
    }

    val statusBar = hbox {
        align(Pos.CENTER_LEFT)
        padding = Insets(0.0, 12.0, 0.0, 12.0)
        prefHeight = 22.0
        styleClass("status-bar")
        spacing = 12.0
        add(calculations)
        hspace()
    }

    val spreadsheet = SpreadsheetView(GridBase(0, 0)).apply {
        selectionModel.selectedCells.addListener(InvalidationListener { onSelectionChanged() })
        columns.forEach { it.setPrefWidth(75.0) }
        zoomFactorProperty().addListener(InvalidationListener { zoomText.value = "${(zoomFactor * 100).toInt()}%" })
        contextMenu = null
        isEditable = false
    }

    val tabBar = hbox { }

    private val tabScroller = vbox {
        padding = Insets(0.0, 0.0, 8.0, 0.0)
        add(scrollPane {
            styleClass("tab-scroller")
            content = tabBar
            isFitToHeight = true
            isFocusTraversable = false
            this.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            this.setOnScroll {
                hvalue = (hvalue + (it.deltaX - it.deltaY) / tabBar.width * 3).coerceIn(0.0, 1.0)
                it.consume()
            }
        })
    }

    val layout = borderPane {
        prefWidth = 720.0
        prefHeight = 480.0
        center = spreadsheet
        bottom = statusBar
    }

    val scene = Scene(layout)

    fun addStatus(prop: StringProperty) {
        statusBar.add(label {
            textProperty().bind(prop)
        })
    }

    fun addTable(table: DataTable) {
        if (tables.isEmpty()) layout.top = tabScroller
        tables.add(table)
        tabBar.children.add(hbox {
            styleClass("tab-item")
            align(Pos.CENTER_LEFT)
            if (table.icon != null) {
                add(fontIcon(table.icon, 14).apply {
                    iconColor = table.iconColor
                })
            }
            add(label(table.title).apply {
                this.maxWidth= 160.0
            })
            add(fontIcon(MaterialDesign.MDI_CLOSE, 14).apply {
                styleClass("tab-close-button")
            })
            setOnMouseClicked {
                val ix = tabBar.children.indexOf(this)
                selectTable(ix)
            }
        })
        selectTable(tabBar.children.size - 1)
    }

    fun selectTable(i: Int) {
        tabBar.children.forEach {
            it.styleClass.remove("tab-item-selected")
        }
        tabBar.children[i].styleClass.add("tab-item-selected")
        val table = tables[i]
        activeTable = table
        stage.title = table.title
        spreadsheet.grid = GridBase(0, 0)
        spreadsheet.grid = table.grid
        spreadsheet.fixedRows.setAll(1)
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

    fun getSelectedColumns(): Set<Int> {
        return spreadsheet.selectionModel.selectedCells.mapTo(HashSet()) { it.column }
    }

    fun getSelectedRows(): Set<Int> {
        return spreadsheet.selectionModel.selectedCells.mapTo(HashSet()) { it.row }
    }

    fun addSort(type: SortType) {
        activeTable?.addSort(getSelectedColumns(), type)
    }

    fun setSort(type: SortType) {
        activeTable?.setSort(getSelectedColumns(), type)
    }

    fun clearSort() {
        activeTable?.clearSort()
    }

    fun addColourScale(type: SortType, rgb: RGB) {
        activeTable?.addColourScale(getSelectedColumns(), type, rgb)
    }

    fun clearColourScale() {
        activeTable?.clearColourScale(getSelectedColumns())
    }

    fun selectColumns() {
        val array = activeTable?.array ?: return
        Singleton.dataSpace.newData(stage.title, Tables.selectColumns(array, getSelectedColumns().toList()))
    }

    fun filterBy() {

    }

    fun filterOut() {

    }

    fun clearFilters() {

    }

    private inline fun copyWithMinMax(block: (grid: GridBase, minRow: Int, maxRow: Int,
                                              minCol: Int, maxCol: Int) -> String) {
        val grid = activeTable?.grid ?: return
        val se = spreadsheet.getSelection()
        val content = ClipboardContent()
        content.putString(block(grid, se.minRow, se.maxRow, se.minCol, se.maxCol))
        Clipboard.getSystemClipboard().setContent(content)
    }

    fun copyDelimited(delimiter: Char) {
        copyWithMinMax { grid, minRow, maxRow, minCol, maxCol ->
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
        val array = activeTable?.array ?: return
        val fp = Singleton.getSavePath(this, "csv")
        if (fp != null) {
            Tables.toCSV(array, fp.outputStream(), ',')
        }
    }

    fun saveZip() {
        val array = activeTable?.array ?: return
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
                val array = activeTable?.array ?: return@InvalidationListener
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
        val a = spreadsheet.selectionModel.selectedCells
        selectionText.value = getRangeText(a)
        if (a.size < 2) {
            calculations.text = ""
            return
        }
        val array = activeTable?.array ?: return
        calculations.text = getCalculations(a, array)
    }
}