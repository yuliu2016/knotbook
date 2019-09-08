package kb.core.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import kb.core.fx.*
import org.kordamp.ikonli.materialdesign.MaterialDesign

class TBAView(owner: Window) {
    val stage = Stage()

    init {
        stage.title = "The Blue Alliance Integration"
        stage.initStyle(StageStyle.UTILITY)
        stage.initOwner(owner)
        stage.isResizable = false

        stage.scene = Scene(vbox {
            prefWidth = 400.0
            padding = Insets(8.0)
            spacing = 8.0

            add(hbox {
                align(Pos.CENTER_LEFT)
                spacing = 8.0
                add(hbox {
                    hgrow()
                    add(Label("TBA not connected"))
                })

                add(Button("", fontIcon(MaterialDesign.MDI_REFRESH, 14)))


                add(CheckBox("Cached"))
            })

            add(hbox {
                spacing = 8.0

                add(TextField("").hgrow().apply {
                    style = "-fx-focus-color: #5a8ade; -fx-padding: 2 6 2 6; -fx-font-weight: bold; -fx-font-size: 13; -fx-faint-focus-color: transparent; -fx-font-family: 'Roboto Mono', monospace;"
                })

                add(Button("", fontIcon(MaterialDesign.MDI_CLOUD_DOWNLOAD, 14)))
            })
        })

        stage.scene.setOnKeyPressed { e ->
            if (e.code == KeyCode.ESCAPE) {
                stage.close()
            }
        }

        stage.show()
    }
}