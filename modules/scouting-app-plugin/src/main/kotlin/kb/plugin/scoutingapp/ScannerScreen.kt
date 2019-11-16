package kb.plugin.scoutingapp

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox
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
        fitWidth = 640.0
        fitHeight = 480.0
    }

    val toggle = CheckBox("Run WebCam Stream")
    val flip = CheckBox("Flip Image Horizontally")
    val cameraChooser = ChoiceBox<String>()
    val driverChooser = ChoiceBox<String>()

    val lv = listView<String> {
        vgrow()
    }

    val sidebar = vbox {
        style = "-fx-background-color: white"
        spacing = 8.0
        padding = Insets(8.0)
        prefWidth = 300.0
        add(cameraChooser)
        add(driverChooser)
        add(toggle)
        add(flip)
        add(hbox {
            spacing = 8.0
            add(button {
                text = "Open File"
            })
            add(button {
                text = "Save As"
            })
        })

        add(lv)
    }

    val layout = borderPane {
        prefWidth = 1000.0
        prefHeight = 600.0
        center = iv
        right = sidebar
    }

    val scene = Scene(layout)

    fun show() {
        scene.onKeyPressed = EventHandler {
            if (it.code == KeyCode.K) {
                camera.isStreaming = !camera.isStreaming
            }
        }
        stage.title = "Scouting App Scanner"
        stage.scene = scene
        iv.imageProperty().bind(camera.imageProperty())
        cameraChooser.items = camera.webcamNames.observable()
        camera.webcamIDProperty.bind(cameraChooser.selectionModel.selectedIndexProperty())
        driverChooser.items = listOf("Deep Space (Scheme V5)", "Infinite Recharge (Scheme V6)", "PowerUp (Scheme V3)").observable()
        camera.streamingProperty().bindBidirectional(toggle.selectedProperty())
        cameraChooser.selectionModel.select(0)
        driverChooser.selectionModel.select(0)
        camera.flippedProperty.bind(flip.selectedProperty())
        camera.isDecoding = true
        stage.showingProperty().addListener { _, _, nv ->
            if (!nv) {
                camera.isStreaming = false
            }
        }

        stage.show()
    }
}