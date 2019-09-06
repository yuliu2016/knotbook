package kb.core.fx

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

fun alertDialog(title: String, message: String) {
    // This enables this to be called from non-fx threads
    runOnFxThread {
        val stage = Stage()
        stage.title = title
        stage.scene = Scene(vbox {
            add(hbox {
                add(Label(message).apply {
                    isWrapText = true
                })
                vgrow()
            })
            prefWidth = 360.0
            minHeight = 90.0
            padding = Insets(16.0)
            add(hbox {
                align(Pos.CENTER_RIGHT)
                padding = Insets(8.0)
                add(button {
                    text = "Close"
                    setOnAction {
                        stage.close()
                    }
                })
            })
        })
        stage.initStyle(StageStyle.UTILITY)
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.isResizable = false
        stage.show()
    }
}