package kb.core.view.app

import javafx.beans.InvalidationListener
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.Separator
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.core.icon.icon
import kb.core.view.DataView
import kb.core.view.splash.Splash
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
            layout.top = menuBar
        } else {
            layout.bottom = null
            layout.top = null
        }
    }

    fun updateTheme() {
        val theme = Singleton.uiManager.themeProperty.get()
        layout.stylesheets.setAll("/knotbook.css", theme.viewStyle)
    }

    val menuBar = menuBar {
        isUseSystemMenuBar = true
    }


    val docLabel = label {
        text = "No Table or Workspace"
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
        top = menuBar
        bottom = statusBar
    }

    val scene = Scene(layout)
    val appIcon = Image(DataView::class.java.getResourceAsStream("/icon.png"))

    fun show() {
        updateTheme()
        Singleton.uiManager.themeProperty.addListener(themeListener)
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

    val helpMenu = fun Modifier<Menu>.() {
        menu {
            name("Help")
            modify {
                item {
                    name("Mark for Garbage Collection")
                    action { Splash.gc() }
                    icon(MaterialDesign.MDI_DELETE_SWEEP, 14)
                    shortcut(KeyCode.B, control = true)
                }
                item {
                    name("JVM Properties")
                    action { Singleton.viewJVMProperties() }
                }
                item {
                    name("Plugins and Services")
                    action { Singleton.viewPlugins() }
                }
                separator()
                item {
                    name("About")
                    action { Splash.info(stage) }
                    icon(MaterialDesign.MDI_INFORMATION_OUTLINE, 14)
                    shortcut(KeyCode.F1)
                }
                item {
                    name("Open Source Licenses")
                    action { Singleton.viewOpenSource() }
                }
            }
        }
    }

    fun addStatus(prop: StringProperty, icon: Ikon) {
        statusBar.add(Separator(Orientation.VERTICAL))
        statusBar.add(label {
            textProperty().bind(prop)
            this.graphic = fontIcon(icon, 14)
        })
    }
}