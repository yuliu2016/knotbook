package kb.core.view.app

import javafx.beans.InvalidationListener
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Separator
import javafx.scene.image.Image
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.core.view.DataView
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign.MaterialDesign

@Suppress("MemberVisibilityCanBePrivate")
class WindowBase {

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
        graphic = fontIcon(MaterialDesign.MDI_FOLDER_MULTIPLE_OUTLINE, 14)
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

    fun show() {
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
                Singleton.uiManager.focusedWindow = this
            } else if (Singleton.uiManager.focusedWindow === this) {
                Singleton.uiManager.focusedWindow = null
            }
        }
        stage.setOnCloseRequest {
            Singleton.uiManager.themeProperty.removeListener(themeListener)
        }
        stage.show()
    }

    fun addStatus(prop: StringProperty, icon: Ikon) {
        statusBar.add(Separator(Orientation.VERTICAL))
        statusBar.add(label {
            textProperty().bind(prop)
            this.graphic = fontIcon(icon, 14)
        })
    }
}