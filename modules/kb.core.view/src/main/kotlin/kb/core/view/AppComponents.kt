package kb.core.view

import javafx.geometry.Insets
import javafx.scene.control.Label
import kb.core.icon.fontIcon
import kb.core.view.app.Singleton
import org.kordamp.ikonli.materialdesign.MaterialDesign

class AppComponents {
    val calcLabel = Label("Average=3.0").apply {
        padding = Insets.EMPTY
        this.graphic = fontIcon(MaterialDesign.MDI_CALCULATOR, 14)
    }

    val selectionLabel = Label("A1:C8").apply {
        padding = Insets.EMPTY
        this.graphic = fontIcon(MaterialDesign.MDI_MOUSE, 14)
    }

    val themeLabel = Label("Light").apply {
        padding = Insets.EMPTY
        this.graphic = fontIcon(MaterialDesign.MDI_COMPARE, 14)
    }

    val zoomLabel = Label("100%").apply {
        padding = Insets.EMPTY
        this.graphic = fontIcon(MaterialDesign.MDI_MAGNIFY_PLUS, 14)
    }

    val heapLabel = Label("24M").apply {
        padding = Insets.EMPTY
        textProperty().bind(Singleton.memoryUsed)
        this.graphic = fontIcon(MaterialDesign.MDI_MEMORY, 14)
    }
}