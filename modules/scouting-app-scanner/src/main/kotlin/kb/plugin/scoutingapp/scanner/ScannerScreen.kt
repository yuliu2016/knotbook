package kb.plugin.scoutingapp.scanner

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.KeyCode
import javafx.stage.FileChooser
import javafx.stage.Stage
import kb.core.camera.fx.FXCamera
import kb.core.fx.*
import kb.service.api.array.NaturalOrderComparator
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


@Suppress("MemberVisibilityCanBePrivate")
class ScannerScreen {
    val stage = Stage()

    val camera = FXCamera()

    var savePath: Path? = null

    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val entriesLock = ReentrantLock()
    val unsortedEntries: MutableList<V5Entry> = ArrayList()
    val textEntries: MutableList<String> = ArrayList()
    val comparator = NaturalOrderComparator()


    fun alert(s: String) {
        val dialog = Dialog<ButtonType>()
        val pane = dialog.dialogPane
        pane.buttonTypes.addAll(ButtonType.OK)
        dialog.dialogPane.content = label(s)
        dialog.title = "Info"
        dialog.showAndWait()
    }

    fun confirm(s: String): Boolean {
        val alert = Alert(AlertType.CONFIRMATION, s, ButtonType.YES, ButtonType.NO)
        alert.showAndWait()
        return alert.result == ButtonType.YES
    }

    fun save() {
        val savePath = savePath ?: return
        saveState.text = "Saving"
        executor.submit {
            try {
                val data = entriesLock.withLock { textEntries.joinToString("\n") }
                Files.writeString(savePath, data)
                Platform.runLater { saveState.text = "AutoSaved" }
            } catch (e: Exception) {
                Platform.runLater { saveState.text = "AutoSave Error" }
            }
        }
    }

    fun removeSelection() {
        val i = tv.selectionModel.selectedIndex
        if (i != -1) {
            if (!confirm("Delete Entry? This Cannot be Undone.")) return
            val originalIndex = if (sortByMatch.isSelected) unsortedEntries.indexOf(tv.items[i]) else i
            unsortedEntries.removeAt(originalIndex)
            tv.items.removeAt(i)
            entriesLock.withLock { textEntries.removeAt(originalIndex) }
            save()
        }
    }

    fun updateTitle() {
        val path = savePath
        stage.title = if (path == null) "Scouting App Scanner" else "Scouting App Scanner | $path"
    }

    fun updateCameras() {
        var i = cameraChooser.selectionModel.selectedIndex
        val names = camera.webcamNames
        if (names.isEmpty()) return
        if (i < 0 || i > names.size) i = 0
        cameraChooser.items = names.observable()
        cameraChooser.selectionModel.select(i)
    }

    fun openFile() {
        if (textEntries.isNotEmpty() && !confirm("Override all current entries?")) return
        val chooser = FileChooser()
        chooser.title = "Save As"
        chooser.initialDirectory = File(System.getProperty("user.home"), "Desktop")
        val path = chooser.showOpenDialog(stage)?.toPath() ?: return
        savePath = path
        saveState.text = "Loading"
        updateTitle()
        textEntries.clear()
        unsortedEntries.clear()
        executor.submit {
            val data = Files.readAllLines(path)
            var i = 0
            val items = ArrayList<V5Entry>()
            val entries = ArrayList<String>()
            data.forEach {
                try {
                    val entry = DecodedEntry(it)
                    items.add(entry)
                    entries.add(it)
                    i++
                } catch (e: Exception) {
                }
            }
            entriesLock.withLock { textEntries.addAll(entries) }
            Platform.runLater {
                unsortedEntries.addAll(items)
                updateSorted(sortByMatch.isSelected)
                saveState.text = "Save File Loaded"
                alert("Loaded $i entries out of ${data.size} lines")
            }
        }
    }

    fun saveAs() {
        val chooser = FileChooser()
        chooser.title = "Save As"
        chooser.initialDirectory = File(System.getProperty("user.home"), "Desktop")
        savePath = chooser.showSaveDialog(stage)?.toPath() ?: return
        updateTitle()
        save()
    }

    fun closeSaveFile() {
        savePath = null
        textEntries.clear()
        unsortedEntries.clear()
        tv.items.clear()
        updateTitle()
        saveState.text = "Save File Closed"
    }

    fun showComment() {
        val i = tv.selectionModel.selectedIndex
        if (i != -1) {
            alert(tv.items[i].comments)
        }
    }

    fun showScoutStats() {
        val map = HashMap<String, MutableList<String>>()
        unsortedEntries.forEach {
            val matchNum = it.match.split("_").last()
            if (it.scout in map) {
                map[it.scout]!!.add(matchNum)
            } else {
                map[it.scout] = mutableListOf(matchNum)
            }
        }
        val entries = map.entries.sortedByDescending { it.value.size }
        val s = entries.joinToString("\n") {
            it.value.sortWith(comparator)
            val size = it.value.size
            val sizeStr = if (size == 1) "1 total match" else "$size total matches"
            "${it.key}: $sizeStr. Last: ${it.value.last()}"
        }
        alert(s)
    }

    fun showWarnings() {
        val map = HashMap<String, MutableList<Board>>()
        unsortedEntries.forEach {
            val matchNum = it.match.split("_").last()
            if (matchNum in map) {
                map[matchNum]!!.add(it.board)
            } else {
                map[matchNum] = mutableListOf(it.board)
            }
        }
        val order = map.keys.sortedWith(comparator)
        val w = StringBuilder()
        val v = Board.values().toMutableList()
        v.remove(Board.RX)
        v.remove(Board.BX)
        val missing = mutableListOf<Board>()
        order.forEach { key ->
            missing.clear()
            val md = map[key]!!
            v.forEach { board -> if (!md.contains(board)) missing.add(board) }
            if (missing.isNotEmpty()) {
                w.append("Match ").append(key).append(": Missing ")
                for (i in 0 until missing.size - 1) w.append(missing[i]).append(", ")
                w.append(missing.last()).append("\n")
            }
        }
        alert(w.toString())
    }

    fun updateSorted(sorted: Boolean) {
        if (sorted) {
            tv.items.setAll(unsortedEntries.sortedWith(Comparator { o1, o2 ->
                comparator.compare(o1.match, o2.match)
            }))
        } else {
            tv.items.setAll(unsortedEntries)
        }
    }

    val iv = imageView {
        isPreserveRatio = true
        fitHeight = 480.0
    }

    val ivCont = hbox {
        align(Pos.CENTER)
        add(iv)
    }

    val cameraChooser = ChoiceBox<String>()
    val streaming = CheckBox("Run WebCam Stream")
    val flip = CheckBox("Flip Image Horizontally")
    val fit = CheckBox("Fit Image to Window")
    val sortByMatch = CheckBox("Sort By Match")

    val saveState = label("No Save File")

    val tv = tableView<V5Entry> {
        vgrow()
        columns.add(tableColumn<V5Entry, String> {
            text = "Match"
            isSortable = false
            prefWidth = 45.0
            setCellValueFactory { SimpleStringProperty(it.value.match.split("_").last()) }
            setCellFactory { MatchCell() }
        })
        columns.add(tableColumn<V5Entry, String> {
            text = "Scout"
            isSortable = false
            prefWidth = 75.0
            setCellValueFactory { SimpleStringProperty(it.value.scout) }
        })
        columns.add(tableColumn<V5Entry, String> {
            text = "Board"
            isSortable = false
            prefWidth = 45.0
            setCellValueFactory { SimpleStringProperty(it.value.board.name) }
        })
        columns.add(tableColumn<V5Entry, String> {
            text = "Team"
            prefWidth = 45.0
            setCellValueFactory { SimpleStringProperty(it.value.team) }
            setCellFactory { TeamCell() }
            isSortable = false
        })
        columns.add(tableColumn<V5Entry, String> {
            isSortable = false
            text = "Comments"
            setCellValueFactory { SimpleStringProperty(it.value.comments) }
        })
    }

    val sidebar = vbox {
        style = "-fx-background-color: white"
        spacing = 8.0
        padding = Insets(8.0)
        prefWidth = 360.0
        add(hbox {
            add(cameraChooser)
            spacing = 8.0
            add(button {
                text = "Refresh"
                setOnAction { updateCameras() }
            })
        })
        add(hbox {
            spacing = 8.0
            add(streaming)
            add(fit)
        })
        add(hbox {
            spacing = 8.0
            add(flip)
            add(sortByMatch)
        })
        add(hbox {
            spacing = 8.0
            add(button {
                text = "Missing Entries"
                setOnAction { showWarnings() }
            })
            add(button {
                text = "Scout Stats"
                setOnAction { showScoutStats() }
            })
        })
        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 8.0
            add(button {
                text = "Open File"
                setOnAction { openFile() }
            })
            add(button {
                text = "Save As"
                setOnAction { saveAs() }
            })
            add(button {
                text = "Close Save File"
                setOnAction { closeSaveFile() }
            })
        })
        add(saveState)
        add(tv)
        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 8.0
            add(button {
                text = "Deselect"
                setOnAction { tv.selectionModel.clearSelection() }
            })
            add(button {
                text = "Delete Selected"
                setOnAction { removeSelection() }
            })
            add(button {
                text = "Show Comment"
                setOnAction { showComment() }
            })
        })
    }

    val layout = borderPane {
        prefWidth = 1080.0
        prefHeight = 600.0
        center = ivCont
        right = sidebar
    }

    val scene = Scene(layout)

    fun onQRCodeResult(encoded: String) {
        if (encoded !in textEntries) {
            try {
                val decoded = DecodedEntry(encoded)
                unsortedEntries.add(decoded)
                if (sortByMatch.isSelected) {
                    updateSorted(true)
                } else {
                    tv.items.add(decoded)
                }
                entriesLock.withLock { textEntries.add(encoded) }
                tv.selectionModel.select(decoded)
                tv.scrollTo(decoded)
                tv.requestFocus()
                save()
            } catch (e: Exception) {
            }
        }
    }

    fun show() {
        stage.show()
    }

    init {
        stage.title = "Scouting App Scanner"
        stage.scene = scene
        scene.onKeyPressed = EventHandler {
            if (it.code == KeyCode.K) {
                streaming.isSelected = !streaming.isSelected
            }
        }

        fit.selectedProperty().addListener { _, _, nv ->
            if (nv) {
                iv.fitHeightProperty().bind(ivCont.heightProperty())
                iv.fitWidthProperty().bind(ivCont.widthProperty())
            } else {
                iv.fitHeightProperty().unbind()
                iv.fitWidthProperty().unbind()
                iv.fitHeight = 480.0
            }
        }

        sortByMatch.selectedProperty().addListener { _, _, nv ->
            updateSorted(nv)
        }

        camera.resultProperty().addListener { _, _, nv ->
            if (nv != null && nv.isNotEmpty()) {
                onQRCodeResult(nv)
            }
        }

        updateCameras()
        camera.webcamIDProperty.bind(cameraChooser.selectionModel.selectedIndexProperty())

        iv.imageProperty().bind(camera.imageProperty())
        camera.streamingProperty().bind(streaming.selectedProperty())
        camera.flippedProperty.bind(flip.selectedProperty())

        camera.isDecoding = true

        stage.focusedProperty().addListener { _, _, nv ->
            if (!nv) {
                streaming.isSelected = false
                Runtime.getRuntime().gc()
            }
        }
    }
}