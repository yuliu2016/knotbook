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

    val iv = imageView {
        isPreserveRatio = true
        fitHeight = 480.0
    }

    val ivCont = hbox {
        align(Pos.CENTER)
        add(iv)
    }

    val toggle = CheckBox("Run WebCam Stream")
    val flip = CheckBox("Flip Image Horizontally")
    val fit = CheckBox("Fit Image to Window")
    val cameraChooser = ChoiceBox<String>()

    class TeamCell : TableCell<V5Entry, String>() {
        override fun updateItem(item: String?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item == null || empty) {
                graphic = null
                return
            }
            graphic = label {
                text = item
                val entry = tableRow?.item
                if (entry != null) {
                    style = when (entry.board.alliance) {
                        Alliance.Red -> "-fx-font-weight: bold; -fx-text-fill: red"
                        Alliance.Blue -> "-fx-font-weight: bold; -fx-text-fill: blue"
                    }
                }
            }
        }
    }

    val tv = tableView<V5Entry> {
        vgrow()

        columns.add(tableColumn<V5Entry, String> {
            text = "Match"
            isSortable = false
            prefWidth = 45.0
            setCellValueFactory { SimpleStringProperty(it.value.match.split("_").last()) }
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
            text = "Scout"
            isSortable = false
            prefWidth = 75.0
            setCellValueFactory { SimpleStringProperty(it.value.scout) }
        })
        columns.add(tableColumn<V5Entry, String> {
            isSortable = false
            text = "Comments"
            setCellValueFactory { SimpleStringProperty(it.value.comments) }
        })
    }

    val saveState = label("No Save File")

    var savePath: Path? = null

    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val entriesLock = ReentrantLock()
    val previousEntries: MutableList<String> = ArrayList()

    fun save() {
        val savePath = savePath ?: return
        saveState.text = "Saving"
        executor.submit {
            try {
                entriesLock.withLock {
                    Files.writeString(savePath, previousEntries.joinToString("\n"))
                }
                Platform.runLater { saveState.text = "AutoSaved" }
            } catch (e: Exception) {
                Platform.runLater { saveState.text = "AutoSave Error" }
            }
        }
    }

    fun removeSelection() {
        val i = tv.selectionModel.selectedIndex
        if (i != -1) {
            val alert = Alert(AlertType.CONFIRMATION, "Delete Entry? This Cannot be Undone.",
                    ButtonType.YES, ButtonType.NO)
            alert.showAndWait()
            if (alert.result != ButtonType.YES) {
                return
            }
            tv.items.removeAt(i)
            entriesLock.withLock {
                previousEntries.removeAt(i)
            }
            save()
        }
    }

    fun updateTitle() {
        val sf = savePath?.toString() ?: "No Save File"
        stage.title = "Scouting App Scanner | $sf"
    }

    fun openFile() {
        if (previousEntries.isNotEmpty()) {
            val alert = Alert(AlertType.CONFIRMATION, "Override all current entries?",
                    ButtonType.YES, ButtonType.NO)
            alert.showAndWait()
            if (alert.result != ButtonType.YES) {
                return
            }
        }
        val chooser = FileChooser()
        chooser.title = "Save As"
        chooser.initialDirectory = File(System.getProperty("user.home"), "Desktop")
        val path = chooser.showOpenDialog(stage)?.toPath() ?: return
        savePath = path
        updateTitle()

        previousEntries.clear()
        tv.items.clear()
        val data = Files.readAllLines(path)
        var i = 0
        entriesLock.withLock {
            data.forEach {
                try {
                    val entry = DecodedEntry(it)
                    previousEntries.add(it)
                    tv.items.add(entry)
                    i++
                } catch (e: Exception) {
                }
            }
        }
        saveState.text = "Save File Loaded"
        val alert = Alert(AlertType.INFORMATION, "Loaded $i entries out of ${data.size} lines")
        alert.showAndWait()
    }

    fun saveAs() {
        val chooser = FileChooser()
        chooser.title = "Save As"
        chooser.initialDirectory = File(System.getProperty("user.home"), "Desktop")
        savePath = chooser.showSaveDialog(stage)?.toPath() ?: return
        updateTitle()
        save()
    }

    val sidebar = vbox {
        style = "-fx-background-color: white"
        spacing = 8.0
        padding = Insets(8.0)
        prefWidth = 360.0
        add(cameraChooser)
        add(toggle)
        add(flip)
        add(fit)
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
            add(saveState)
        })
        add(tv)
        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 8.0
            add(button {
                text = "Unselect Entry"
                setOnAction { tv.selectionModel.clearSelection() }
            })
            add(button {
                text = "Delete Selected Entry"
                setOnAction { removeSelection() }
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
                entriesLock.withLock {
                    previousEntries.add(encoded)
                }
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
        iv.imageProperty().bind(camera.imageProperty())
        cameraChooser.items = camera.webcamNames.observable()
        camera.webcamIDProperty.bind(cameraChooser.selectionModel.selectedIndexProperty())
        camera.streamingProperty().bindBidirectional(toggle.selectedProperty())
        cameraChooser.selectionModel.select(0)
        camera.flippedProperty.bind(flip.selectedProperty())
        camera.isDecoding = true
        stage.showingProperty().addListener { _, _, nv ->
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