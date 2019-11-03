package kb.core.view.app

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.Separator
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Popup
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.core.icon.icon
import kb.core.view.DataView
import kb.core.view.splash.AboutSplash
import kb.core.view.splash.GCSplash
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign.MaterialDesign

@Suppress("MemberVisibilityCanBePrivate")
class WindowBase {

    companion object {
        const val kOBWidth = 600.0
        const val kOBHeight = 480.0
    }

    val stage = Stage()

    private var isFullScreen = false

    fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        stage.isFullScreen = isFullScreen
    }

    fun showOptionBarPrototype() {
        val popup = Popup()
        popup.content.add(vbox {
            stylesheets.addAll("/knotbook.css", themeProperty.get().optionStyle)
            styleClass("option-bar")
            effect = DropShadow().apply {
                color = Color.GRAY
                blurType = BlurType.GAUSSIAN
                height = 10.0
                width = 10.0
                radius = 10.0
                offsetY = 5.0
            }
            prefWidth = kOBWidth
            prefHeight = kOBHeight
            add(vbox {
                align(Pos.TOP_CENTER)
                padding = Insets(8.0)
                spacing = 4.0
                add(textField {
                    styleClass("formula-field")
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
        popup.x = stage.x + stage.width / 2.0 - kOBWidth / 2.0
        popup.y = stage.y + scene.y + menuBar.height - 5.0
        popup.show(stage)
    }

    val themeProperty = SimpleObjectProperty(Theme.Light)

    fun toggleTheme() {
        themeProperty.set(when (themeProperty.get()!!) {
            Theme.Light -> Theme.Dark
            Theme.Dark -> Theme.Light
        })
        layout.stylesheets.setAll("/knotbook.css", themeProperty.get().viewStyle)
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
        stylesheets.addAll("/knotbook.css", Theme.Light.viewStyle)
        prefWidth = 510.0
        prefHeight = 340.0
        top = menuBar
        bottom = statusBar
        isSnapToPixel = false
    }

    val scene = Scene(layout)
    val appIcon = Image(DataView::class.java.getResourceAsStream("/icon.png"))

    fun show() {
        stage.fullScreenExitHint = "Press F11 to Exit Full Screen"
        stage.title = "KnotBook"
        stage.icons.add(appIcon)
        stage.scene = scene
        stage.focusedProperty().addListener { _, _, focused ->
            if (focused) {
                Singleton.focusedWindow = this
            } else if (Singleton.focusedWindow === this) {
                Singleton.focusedWindow = null
            }
        }
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

    fun addStatus(prop: StringProperty, icon: Ikon) {
        statusBar.add(Separator(Orientation.VERTICAL))
        statusBar.add(label {
            textProperty().bind(prop)
            this.graphic = fontIcon(icon, 14)
        })
    }
}