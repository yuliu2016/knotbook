package kb.plugin.appscanner

import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TableCell
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import kb.core.camera.fx.FXCamera
import kb.core.fx.*

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
            setCellFactory {
                object : TableCell<V5Entry, String>() {
                    override fun updateItem(item: String?, empty: Boolean) {
                        super.updateItem(item, empty)
                        if (item == null || empty) {
                            graphic = null
                            return
                        }
                        graphic = label {
                            text = item
                            tableRow.item?.let {
                                style = when (it.board.alliance) {
                                    Alliance.Red -> "-fx-font-weight: bold; -fx-text-fill: red"
                                    Alliance.Blue -> "-fx-font-weight: bold; -fx-text-fill: blue"
                                }
                            }
                        }
                    }
                }
            }
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

    val saveState = label("AutoSaved")

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
            })
            add(button {
                text = "Save As"
            })
            add(saveState)
        })
        add(tv)
        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 8.0
            add(button {
                text = "Clear Selection"
                setOnAction {
                    tv.selectionModel.clearSelection()
                }
            })
            add(button {
                text = "Remove Selection"
                setOnAction {
                    val i = tv.selectionModel.selectedIndex
                    if (i != -1) {
                        tv.items.removeAt(i)
                        previousEntries.removeAt(i)
                    }
                }
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

    val previousEntries: MutableList<String> = ArrayList()

    fun onQRCodeResult(encoded: String) {
        if (encoded !in previousEntries) {
            try {
                val decoded = DecodedEntry(encoded)
                tv.items.add(decoded)
                previousEntries.add(encoded)
                tv.scrollTo(tv.items.size - 1)
            } catch (e: Exception) {
            }
        }
    }

    fun show() {
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
        stage.show()
    }
}