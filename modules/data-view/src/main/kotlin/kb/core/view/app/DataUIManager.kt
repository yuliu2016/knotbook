package kb.core.view.app

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kb.core.view.DataView
import kb.core.view.splash.Splash
import kb.service.api.ui.*
import java.util.*
import java.util.function.Consumer
import kotlin.concurrent.thread


@Suppress("MemberVisibilityCanBePrivate")
class DataUIManager : UIManager {

    enum class Theme(val viewStyle: String, val optionStyle: String) {
        Light("/light.css", "/light-option.css"),
        Dark("/dark.css", "/dark-option.css");
    }

    val memoryUsed = SimpleStringProperty()
    val themeProperty = SimpleObjectProperty(Theme.Dark)

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
            null -> Theme.Dark
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
        UIHelper.run { Splash.alert(view?.stage, title, message, false) }
    }

    fun showAlertMonospace(title: String, message: String) {
        UIHelper.run { Splash.alert(view?.stage, title, message, true) }
    }

    override fun showException(e: Throwable?) {
        val thread = Thread.currentThread()
        UIHelper.run { Splash.error(view?.stage, thread, e) }
    }

    override fun confirmOK(title: String, message: String, runIfOk: Runnable?) {
        UIHelper.run { if (Splash.confirmOK(view?.stage, title, message)) runIfOk?.run() }
    }

    override fun confirmYes(title: String, message: String, runIfYes: Runnable?) {
        UIHelper.run { if (Splash.confirmYes(view?.stage, title, message)) runIfYes?.run() }
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

    fun startMemoryObserver() {
        thread(isDaemon = true, name = "KnotBook Memory Observer") {
            var lastMemoryUsed = -1
            while (true) {
                val memoryUsed = ((Runtime.getRuntime().totalMemory() -
                        Runtime.getRuntime().freeMemory()) / 1024.0 / 1024.0).toInt() + 1
                if (memoryUsed != lastMemoryUsed) {
                    Platform.runLater {
                        this.memoryUsed.value = "${memoryUsed}M"
                    }
                    lastMemoryUsed = memoryUsed
                }
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
    }
}