package kb.core.view.app

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.scene.control.Alert
import javafx.scene.input.KeyCode
import javafx.stage.Window
import kb.core.fx.combo
import kb.core.view.DataView
import kb.core.view.server.Server
import kb.core.view.splash.Splash
import kb.service.api.ServiceContext
import kb.service.api.application.ServiceManager
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * Singleton object representing the application
 *
 * Runs on JavaFX application thread
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal object Singleton {

    private var nullableManager: ServiceManager? = null
    private var nullableContext: ServiceContext? = null

    val manager get() = nullableManager!!
    val context get() = nullableContext!!

    val dataServer = Server()
    val uiManager = ViewManager()

    fun editAppProperties() {
        context.createTextEditor()
                .editable()
                .withSyntax("text/json")
                .withTitle("Application Properties")
                .withInitialText(manager.props.joinedText)
                .addAction("Save Changes") { changed, finalText ->
                    if (changed) {
                        manager.props.setInputText(finalText)
                    }
                }
                .show()
    }

    fun viewJVMProperties() {
        val properties = System
                .getProperties()
                .entries
                .sortedBy { it.key.toString() }
                .joinToString("\n") {
                    val strVal = it.value.toString()
                    val value = when {
                        strVal.endsWith("\\") -> "'$strVal'"
                        strVal == System.lineSeparator() -> "LINE_SEPARATOR"
                        else -> strVal
                    }
                    "${it.key}=$value"
                }
        context.createTextEditor()
                .withTitle("JVM Properties (Read-Only)")
                .withSyntax("text/properties")
                .withInitialText(properties)
                .show()
    }

    fun viewPlugins() {
        val t = manager.services.joinToString("\n") {
            it.metadata.run { "$packageName => $packageVersion" }
        }
        context.createTextEditor()
                .withTitle("Plugins and Services")
                .withInitialText(t)
                .show()
    }

    fun viewOpenSource() {
        val t = Singleton::class.java
                .getResourceAsStream("/open_source.txt")
                .use { it.bufferedReader().readText() }
        context.createTextEditor()
                .withTitle("Open Source Licences")
                .withInitialText(t)
                .show()
    }

    fun startMemoryObserver() {
        thread(isDaemon = true, name = "MemoryObserver") {
            var lastMemoryUsed = -1
            while (true) {
                val memoryUsed = ((Runtime.getRuntime().totalMemory() -
                        Runtime.getRuntime().freeMemory()) / 1024.0 / 1024.0).toInt() + 1
                if (memoryUsed != lastMemoryUsed) {
                    Platform.runLater {
                        uiManager.memoryUsed.value = "${memoryUsed}M"
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

    fun launch(manager: ServiceManager, context: ServiceContext) {
        nullableContext = context
        nullableManager = manager
        launchImpl()
    }

    private fun launchImpl() {
        startMemoryObserver()
        Platform.setImplicitExit(false)
        val windows = Window.getWindows()
        windows.addListener(InvalidationListener { if (windows.isEmpty()) exitOK() })
        try {
            dataServer.bindAndStart()
        } catch (e: IOException) {
            Alert(Alert.AlertType.ERROR, "Application Already Started").showAndWait()
            exitProcess(0)
        }
        launchCommands()
        getList().forEach { context.uiManager.registerCommand(it.name, it) }
        DataView().show()
    }

    private fun launchCommands() {
        val m = context.uiManager
        m.registerCommand("app.about", "About KnotBook", MDI_INFORMATION_OUTLINE.description,
                combo(KeyCode.F1)) { uiManager.focusedWindow?.let { Splash.info(it.stage) } }
        m.registerCommand("nav.recent", "Open Recent", MDI_HISTORY.description,
                combo(KeyCode.R, control = true)) { }
        m.registerCommand("nav.file", "Open File", MDI_FOLDER_OUTLINE.description,
                combo(KeyCode.O, control = true)) { }
        m.registerCommand("window.close", "Close Window", MDI_CLOSE.description,
                combo(KeyCode.W, control = true)) { }
        m.registerCommand("jvm.properties", "JVM Properties",
                MDI_COFFEE.description, null) { viewJVMProperties() }
        m.registerCommand("jvm.gc", "JVM: Run Memory Garbage Collection",
                MDI_DELETE_SWEEP.description, combo(KeyCode.B, control = true)) { Splash.gc() }
        m.registerCommand("app.config", "Settings",
                MDI_TUNE.description, combo(KeyCode.COMMA, control = true)) { editAppProperties() }
        m.registerCommand("theme.toggle", "Toggle Colour Scheme",
                MDI_COMPARE.description, combo(KeyCode.F3)) { uiManager.toggleTheme() }
        m.registerCommand("fullscreen.toggle", "Toggle Full Screen",
                MDI_ARROW_EXPAND.description, combo(KeyCode.F11)
        ) { uiManager.focusedWindow?.toggleFullScreen() }
        m.registerCommand("status.toggle", "Toggle Status Bar",
                null, null) { uiManager.focusedWindow?.toggleStatusBar() }
        m.registerCommand("window.create", "New Window", null,
                combo(KeyCode.N, control = true)) { DataView().show() }
        m.registerCommand("test.python.editor", "Test Python Editor",
                MDI_LANGUAGE_PYTHON.description, null
        ) { context.createTextEditor().withSyntax("text/python").editable().show() }
        m.registerCommand("command.palette", "Command Palette", MDI_CONSOLE.description,
                combo(KeyCode.K, control = true)) { uiManager.showCommandsBar() }
        m.registerCommand("app.license", "Open Source Licenses", null,
                null) { viewOpenSource() }
    }

    fun exitOK() {
        Platform.runLater {
            dataServer.exit()
            Platform.exit()
            manager.exitOK()
        }
    }
}