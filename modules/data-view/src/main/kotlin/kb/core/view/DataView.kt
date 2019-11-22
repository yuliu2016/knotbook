package kb.core.view

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.stage.Screen
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.view.app.Singleton
import kb.service.api.array.TableArray
import kb.service.api.array.TableUtil
import org.controlsfx.control.spreadsheet.SpreadsheetCell
import org.controlsfx.control.spreadsheet.SpreadsheetView


@Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode", "unused")
class DataView {

    val stage = Stage()
    val themeListener = InvalidationListener { updateTheme() }
    private var isFullScreen = false
    private var grid = emptyGrid()

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
    }

    val calculations = label {
        text = "Average: 1.5    Count: 2    Unique: 4    Sum: 3    Min: 4    Max: 8"
    }

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
        selectionModel.selectedCells.addListener(InvalidationListener { selectionText.value = getRangeText() })
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
    val appIcon = Image(DataView::class.java.getResourceAsStream("/icon.png"))
    var showing = false

    fun addStatus(prop: StringProperty) {
        statusBar.add(label {
            textProperty().bind(prop)
        })
    }

    val zoomText = SimpleStringProperty("100%")
    val themeText = SimpleStringProperty("Light")
    val selectionText = SimpleStringProperty("None")

    fun show() {
        if (showing) throw IllegalStateException("DataView is already shown")
        showing = true

        val area = Screen.getPrimary().visualBounds
        layout.prefWidth = area.width / 2.0
        layout.prefHeight = area.height / 2.0 - 32.0

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
        stage.icons.add(appIcon)
        stage.scene = scene
        stage.focusedProperty().addListener { _, _, focused ->
            if (focused) Singleton.uiManager.view = this
            else if (Singleton.uiManager.view === this) Singleton.uiManager.view = null
        }
        stage.setOnCloseRequest { Singleton.uiManager.themeProperty.removeListener(themeListener) }
        stage.show()
    }

    var array: TableArray? = null

    private val sortColumns = ArrayList<SortColumn>()
    private val colourScales = ArrayList<ColorScale>()
    private var referenceOrder = ArrayList<ObservableList<SpreadsheetCell>>()

    fun setData(title: String, data: TableArray) {
        stage.title = title
        array = data
        grid = data.toGrid()
        this.spreadsheet.grid = grid
        spreadsheet.fixedRows.setAll(0)
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

    private fun getRangeText(): String {
        val a = spreadsheet.selectionModel.selectedCells
        if (a.isEmpty()) return "None"
        val rows = a.map { it.row }
        val cols = a.map { it.column }
        val w = rows.min()!! + 1
        val x = rows.max()!! + 1
        val y = TableUtil.columnIndexToString(cols.min()!!)
        val z = TableUtil.columnIndexToString(cols.max()!!)
        return if (a.size == 1) "$y$w" else "$y$w:$z$x"
    }
}