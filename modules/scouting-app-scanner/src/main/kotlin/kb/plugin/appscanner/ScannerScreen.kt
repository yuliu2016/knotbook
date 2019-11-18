package kb.plugin.appscanner

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
    val previousEntries: MutableList<String> = ArrayList()


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
                val data = entriesLock.withLock { previousEntries.joinToString("\n") }
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
            tv.items.removeAt(i)
            entriesLock.withLock { previousEntries.removeAt(i) }
            save()
        }
    }

    fun updateTitle() {
        val path = savePath
        stage.title = if (path == null) "Scouting App Scanner" else
            "Scouting App Scanner | $path"
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
        if (previousEntries.isNotEmpty() && !confirm("Override all current entries?")) return
        val chooser = FileChooser()
        chooser.title = "Save As"
        chooser.initialDirectory = File(System.getProperty("user.home"), "Desktop")
        val path = chooser.showOpenDialog(stage)?.toPath() ?: return
        savePath = path
        saveState.text = "Loading"
        updateTitle()
        previousEntries.clear()
        tv.items.clear()
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
            entriesLock.withLock { previousEntries.addAll(entries) }
            Platform.runLater {
                tv.items.setAll(items)
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

    fun showComment() {
        val i = tv.selectionModel.selectedIndex
        if (i != -1) {
            alert(tv.items[i].comments)
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
    val toggle = CheckBox("Run WebCam Stream")
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
            add(toggle)
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
                text = "Show Warnings"
            })
            add(button {
                text = "Scout Stats"
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
                setOnAction {
                    savePath = null
                    previousEntries.clear()
                    tv.items.clear()
                    updateTitle()
                    saveState.text = "Save File Closed"
                }
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
        if (encoded !in previousEntries) {
            try {
                val decoded = DecodedEntry(encoded)
                tv.items.add(decoded)
                entriesLock.withLock { previousEntries.add(encoded) }
                tv.scrollTo(tv.items.size - 1)
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
                camera.isStreaming = !camera.isStreaming
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

        camera.resultProperty().addListener { _, _, nv ->
            if (nv != null && nv.isNotEmpty()) {
                onQRCodeResult(nv)
            }
        }

        updateCameras()
        camera.webcamIDProperty.bind(cameraChooser.selectionModel.selectedIndexProperty())

        iv.imageProperty().bind(camera.imageProperty())
        camera.streamingProperty().bindBidirectional(toggle.selectedProperty())
        camera.flippedProperty.bind(flip.selectedProperty())

        camera.isDecoding = true

        stage.focusedProperty().addListener { _, _, nv ->
            if (!nv) {
                camera.isStreaming = false
            }
        }
        camera.streamingProperty().addListener { _, _, nv ->
            if (!nv) {
                Runtime.getRuntime().gc()
            }
        }
    }
}