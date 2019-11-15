package kb.core.view

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Separator
import javafx.scene.image.Image
import javafx.stage.FileChooser
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.core.view.app.Singleton
import kb.service.api.array.TableArray
import kb.service.api.array.TableUtil
import org.controlsfx.control.spreadsheet.SpreadsheetView
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.io.FileInputStream


@Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode", "unused")
class DataView {

    val stage = Stage()

    val themeListener = InvalidationListener {
        updateTheme()
    }

    private var isFullScreen = false

    fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        stage.isFullScreen = isFullScreen
    }

    fun toggleStatusBar() {
        if (layout.bottom == null) {
            layout.bottom = statusBar
        } else {
            layout.bottom = null
        }
    }

    fun updateTheme() {
        val theme = Singleton.uiManager.themeProperty.get()
        layout.stylesheets.setAll("/knotbook.css", theme.viewStyle)
    }

    val docLabel = label {
        text = ""
        graphic = fontIcon(MDI_FOLDER_MULTIPLE_OUTLINE, 14)
    }

    private val statusBar = hbox {
        align(Pos.CENTER_LEFT)
        padding = Insets(0.0, 8.0, 0.0, 8.0)
        prefHeight = 22.0
        styleClass("status-bar")
        spacing = 8.0
        add(docLabel)
        hspace()
    }

    val layout = borderPane {
        prefWidth = 720.0
        prefHeight = 480.0
        bottom = statusBar
    }

    val scene = Scene(layout)
    val appIcon = Image(DataView::class.java.getResourceAsStream("/icon.png"))
    var showing = false

    fun addStatus(prop: StringProperty, icon: Ikon) {
        statusBar.add(Separator(Orientation.VERTICAL))
        statusBar.add(label {
            textProperty().bind(prop)
            this.graphic = fontIcon(icon, 14)
        })
    }

    fun tableFromFile() {
        val fc = FileChooser()
        fc.title = "Open Table from File"
        val f = fc.showOpenDialog(stage)
        if (f != null && f.extension == "csv") {
            docLabel.text = "Loading"
            Thread {
                try {
                    val a = TableArray.fromCSV(FileInputStream(f), true)
                    runOnFxThread {
                        stage.isMaximized = true
                        spreadsheet.grid = a.toGrid()
                        spreadsheet.fixedRows.setAll(0)
                        docLabel.text = f.absolutePath
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    val zoomText = SimpleStringProperty("100%")
    val themeText = SimpleStringProperty("Light")
    val selectionText = SimpleStringProperty("None")

    private val spreadsheet = SpreadsheetView(emptyGrid()).apply {
        selectionModel.selectedCells.addListener(InvalidationListener {
            selectionText.value = getRange()
        })
        columns.forEach {
            it.setPrefWidth(75.0)
        }
        zoomFactorProperty().addListener { _, _, nv ->
            zoomText.value = "${(nv.toDouble() * 100).toInt()}%"
        }
        contextMenu = null
        Platform.runLater {
            columns.forEach {
                it.minWidth = 42.0
            }
        }
    }

    fun show() {
        layout.center = spreadsheet
        themeText.bind(Singleton.uiManager.themeProperty.asString())
        addStatus(selectionText, MDI_MOUSE)
        addStatus(zoomText, MDI_MAGNIFY_PLUS)
        addStatus(themeText, MDI_COMPARE)
        addStatus(Singleton.uiManager.memoryUsed, MDI_MEMORY)
        showImpl()
    }


    fun showImpl() {
        if (showing) {
            return
        }
        showing = true
        updateTheme()
        Singleton.uiManager.themeProperty.addListener(themeListener)
        Singleton.uiManager.commandManager.forEachShortcut { shortcut, key ->
            scene.accelerators[shortcut] = Runnable {
                Singleton.uiManager.commandManager.invokeCommand(key)
            }
        }
        stage.fullScreenExitHint = "Press F11 to Exit Full Screen"
        stage.title = "KnotBook"
        stage.icons.add(appIcon)
        stage.scene = scene
        stage.focusedProperty().addListener { _, _, focused ->
            if (focused) {
                Singleton.uiManager.view = this
            } else if (Singleton.uiManager.view === this) {
                Singleton.uiManager.view = null
            }
        }
        stage.setOnCloseRequest {
            Singleton.uiManager.themeProperty.removeListener(themeListener)
        }
        stage.show()
    }

    private fun getRange(): String {
        val a = spreadsheet.selectionModel.selectedCells
        if (a.isEmpty()) {
            return "None"
        }
        val rows = a.map { it.row }
        val cols = a.map { it.column }

        val w = rows.min()!! + 1
        val x = rows.max()!! + 1
        val y = TableUtil.columnIndexToString(cols.min()!!)
        val z = TableUtil.columnIndexToString(cols.max()!!)

        return if (a.size == 1) {
            "$y$w"
        } else {
            "$y$w:$z$x"
        }
    }
}