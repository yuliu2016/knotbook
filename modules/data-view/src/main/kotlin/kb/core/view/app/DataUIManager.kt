package kb.core.view.app

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kb.core.fx.runOnFxThread
import kb.core.view.DataView
import kb.core.view.splash.Splash
import kb.service.api.ui.*
import java.util.*
import java.util.function.Consumer


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

    val textEditors = ServiceLoader.load(TextEditorService::class.java).toList()

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
        runOnFxThread { Splash.alert(title, message) }
    }

    override fun showException(e: Throwable?) {
        val thread = Thread.currentThread()
        runOnFxThread { Splash.error(thread, e) }
    }

    override fun confirmOK(title: String, message: String, runIfOk: Runnable?) {
        runOnFxThread { if (Splash.confirmOK(title, message)) runIfOk?.run() }
    }

    override fun confirmYes(title: String, message: String, runIfYes: Runnable?) {
        runOnFxThread { if (Splash.confirmYes(title, message)) runIfYes?.run() }
    }

    override fun getTextInput(prompt: String, callback: Consumer<String>) {
        val ob = OptionBar()
        ob.hint = prompt

        ob.setOnEnterPressed {
            ob.isShowing = false
            callback.accept(ob.text)
        }

        showOptionBar(ob)
    }

    override fun createTextEditor(): TextEditor {
        return textEditors.first().create().withDarkTheme(isDarkTheme())
    }
}