package kb.core.view

import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import kb.core.fx.add
import kb.core.fx.observable
import kb.core.fx.vbox

class TBAView(owner: Window) {
    val stage = Stage()

    init {
        stage.title = "The Blue Alliance Integration"
        stage.initStyle(StageStyle.UTILITY)
        stage.initOwner(owner)
//        stage.isAlwaysOnTop = true
        stage.isResizable = false

        stage.scene = Scene(vbox {
            prefWidth = 400.0
            prefHeight = 200.0
            padding = Insets(8.0)

            add(Label("TBA is not connected"))

            add(CheckBox("Use Cache"))

            add(Button("Test Connection"))

            add(Button("Change Key"))

            add(Label("Event: "))

            add(TextField(""))

            add(Label("Type: "))

            add(ComboBox<String>(listOf(
                    "Match Schedule",
                    "Rankings",
                    "OPRS",
                    "Ranking Predictions",
                    "Match Data",
                    "Match Results",
                    "Team Media",
                    "Match Predictions"
            ).observable()))

            add(Button("Generate Table"))
        })

        stage.scene.setOnKeyPressed { e ->
            if (e.code == KeyCode.ESCAPE) {
                stage.close()
            }
        }

        stage.show()
    }
}