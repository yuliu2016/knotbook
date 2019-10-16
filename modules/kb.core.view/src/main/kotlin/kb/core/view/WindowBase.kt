package kb.core.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Popup
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.icon
import kb.core.view.app.Singleton
import kb.core.view.splash.AboutSplash
import kb.core.view.splash.GCSplash
import org.kordamp.ikonli.materialdesign.MaterialDesign

class WindowBase {


    val stage = Stage()

    private var isFullScreen = false

    fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        stage.isFullScreen = isFullScreen
    }

    fun showOptionBarPrototype() {
        val popup = Popup()
        popup.content.add(vbox {
            stylesheets.addAll("/knotbook.css", Theme.Light.fileName)
            style = "-fx-background-color: white"
            styleClass("option-bar")
            effect = DropShadow().apply {
                color = Color.GRAY
                height = 10.0
                width = 10.0
                radius = 10.0
            }
            prefWidth = 600.0
            prefHeight = 480.0
            add(vbox {
                align(Pos.TOP_CENTER)
                padding = Insets(8.0)
                spacing = 4.0
                add(textField {
                    styleClass("formula-field")
                    promptText = "Enter a Command or Formula"
                })
            })

            add(listView<Entity> {
                vgrow()
                items = getList().observable()
                setCellFactory {
                    EntityListCell()
                }
            })

        })
        popup.isAutoHide = true
        popup.show(stage)
        popup.centerOnScreen()
    }

    private var theme = Theme.Light

    fun toggleTheme() {
        theme = when (theme) {
            Theme.Light -> Theme.Dark
            Theme.Dark -> Theme.Light
        }
        layout.stylesheets.setAll("/knotbook.css", theme.fileName)
//        components.themeLabel.text = theme.name
    }

    val menuBar = menuBar {
        isUseSystemMenuBar = true
    }

    val layout = borderPane {
        stylesheets.addAll("/knotbook.css", Theme.Light.fileName)
        prefWidth = 1120.0
        prefHeight = 630.0
        top = menuBar
        isSnapToPixel = false
    }

    val scene = Scene(layout)

    fun show() {
        stage.fullScreenExitHint = "Press F11 to Exit Full Screen"
        stage.title = "KnotBook"
        stage.icons.add(Image(DataView::class.java.getResourceAsStream("/icon.png")))
        stage.scene = scene
        stage.show()
    }

    val helpMenu = fun Modifier<Menu>.() {
        menu {
            name("Help")
            modify {
                item {
                    name("Mark for Garbage Collection")
                    action { GCSplash.splash() }
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
                    action { AboutSplash.splash(stage) }
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
}