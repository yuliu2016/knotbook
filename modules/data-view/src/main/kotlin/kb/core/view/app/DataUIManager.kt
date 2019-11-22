package kb.core.view.app

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import kb.core.fx.label
import kb.core.view.DataView
import kb.service.api.ui.Command
import kb.service.api.ui.OptionBar
import kb.service.api.ui.UIManager
import java.io.PrintWriter
import java.io.StringWriter
import java.util.function.Consumer
import java.util.function.Function


@Suppress("MemberVisibilityCanBePrivate")
class DataUIManager : UIManager {

    enum class Theme(val viewStyle: String, val optionStyle: String) {
        Light("/light.css", "/light-option.css"),
        Dark("/dark.css", "/dark-option.css");
    }

    val memoryUsed = SimpleStringProperty()
    val themeProperty = SimpleObjectProperty(Theme.Light)

    val commandManager = CommandManager()
    val stagedOptionBar = StagedOptionBar()

    // The currently focused view
    var view: DataView? = null

    init {
        themeProperty.addListener(InvalidationListener { updateTheme() })
        updateTheme()
    }

    fun toggleTheme() {
        themeProperty.set(when (themeProperty.get()) {
            null -> Theme.Light
            Theme.Light -> Theme.Dark
            Theme.Dark -> Theme.Light
        })
    }

    fun updateTheme() {
        val theme = themeProperty.get()
        stagedOptionBar.setTheme("/knotbook.css", theme.optionStyle)
    }

    fun isDarkTheme(): Boolean {
        return themeProperty.get() == Theme.Dark
    }

    fun showCommandsPalette() {
        commandManager.setAll()
        commandManager.bar.text = ""
        showOptionBar(commandManager.bar)
    }

    override fun isOptionBarShown(): Boolean {
        return stagedOptionBar.isShowing()
    }

    override fun showOptionBar(optionBar: OptionBar) {
        val win = view
        if (win != null) {
            stagedOptionBar.show(optionBar, win.stage)
        }
    }

    override fun registerCommand(id: String, command: Command) {
        commandManager.registerCommand(id, command)
    }

    override fun hasCommand(id: String): Boolean {
        return commandManager.hasCommand(id)
    }

    override fun invokeCommand(id: String) {
        commandManager.invokeCommand(id)
    }

    override fun showAlert(title: String, message: String) {
        val dialog = Dialog<ButtonType>()
        val pane = dialog.dialogPane
        pane.buttonTypes.addAll(ButtonType.OK)
        dialog.dialogPane.content = label(message)
        dialog.title = title
        dialog.showAndWait()
    }

    override fun showException(e: Exception?) {
        if (e == null) return
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        showAlert("Error", sw.toString())
    }

    override fun getTextInput(prompt: String, validator: Function<String, Boolean>?, callback: Consumer<String>) {
        val ob = OptionBar()
        ob.hint = prompt

        ob.setOnEnterPressed {
            ob.isShowing = false
            callback.accept(ob.text)
        }

        showOptionBar(ob)
    }
}