package kb.core.view.app

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.stage.Popup
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.service.api.ui.CommandCallback
import kb.service.api.ui.Notification
import org.kordamp.ikonli.materialdesign.MaterialDesign

class ScreenEvent : Notification {

    private var icon = MaterialDesign.MDI_INFORMATION_OUTLINE
    private val actions = mutableMapOf<String, CommandCallback>()

    private val messageProperty = SimpleStringProperty()

    override fun setInfo() = apply {
        icon = MaterialDesign.MDI_INFORMATION_OUTLINE
    }

    override fun setWarning() = apply {
        icon = MaterialDesign.MDI_ALERT_CIRCLE_OUTLINE
    }

    override fun setError() = apply {
        icon = MaterialDesign.MDI_ALERT_CIRCLE_OUTLINE
    }

    override fun setMessage(message: String) = apply {
        runOnFxThread { messageProperty.set(message) }
    }

    override fun addAction(name: String, callback: CommandCallback) = apply {
        actions[name] = callback
    }

    override fun show() = apply {
        runOnFxThread {
            Singleton.focusedWindow?.let {
                showImpl(it)
            }
        }
    }

    private fun showImpl(base: WindowBase) {
        val popup = Popup()
        val cont = hbox {
            effect = DropShadow()
            style = "-fx-background-color: white"
            add(fontIcon(icon, 14))
            add(label {
                textProperty().bind(messageProperty)
            })
            hspace()
            align(Pos.CENTER_LEFT)
            add(fontIcon(MaterialDesign.MDI_CLOSE, 14).apply {
                this.setOnMouseClicked {
                    popup.hide()
                }
            })
            minWidth = 300.0
            minHeight = 50.0
            padding = Insets(8.0)
            spacing = 8.0
        }
        popup.content.add(cont)
        popup.show(base.stage)
        popup.isAutoHide = true
        popup.x = base.stage.x + base.stage.width - popup.width - 16.0
        popup.y = base.stage.y + base.stage.height - popup.height - 22 - 16.0
    }
}