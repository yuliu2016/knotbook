package kb.core.view.app

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.stage.FileChooser
import javafx.stage.Window
import kb.core.fx.combo
import kb.core.fx.runOnFxThread
import kb.core.view.DataView
import kb.core.view.server.Server
import kb.core.view.splash.Splash
import kb.core.view.toGrid
import kb.service.api.ServiceContext
import kb.service.api.application.ServiceManager
import kb.service.api.array.TableArray
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.io.FileInputStream
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
    val uiManager = DataUIManager()

    fun editAppProperties() {
        context.createTextEditor()
                .editable()
                .withSyntax("text/json")
                .withTitle(manager.props.path.toString())
                .withInitialText(manager.props.joinedText)
                .withDarkTheme(uiManager.isDarkTheme())
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
                .withDarkTheme(uiManager.isDarkTheme())
                .show()
    }

    fun viewPlugins() {
        val t = manager.services.joinToString("\n") {
            it.metadata.run { "$packageName => $packageVersion" }
        }
        context.createTextEditor()
                .withTitle("Plugins and Services")
                .withInitialText(t)
                .withDarkTheme(uiManager.isDarkTheme())
                .show()
    }

    fun viewOpenSource() {
        val t = Singleton::class.java
                .getResourceAsStream("/open_source.txt")
                .use { it.bufferedReader().readText() }
        context.createTextEditor()
                .withTitle("Open Source Licences")
                .withInitialText(t)
                .withDarkTheme(uiManager.isDarkTheme())
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
        launchCommands2()
    }

    private fun closeWindow() {
        uiManager.view?.let { win ->
            val alert = Alert(Alert.AlertType.CONFIRMATION, "Close Window?", ButtonType.YES, ButtonType.NO)
            alert.showAndWait()
            if (alert.result == ButtonType.YES) {
                win.stage.close()
            }
        }
    }

    fun newWindow() {
        val dv = DataView()
        uiManager.view?.let { win ->
            dv.stage.x = win.stage.x + 48.0
            dv.stage.y = win.stage.y + 36.0
            dv.show()
        }
    }

    fun tableFromFile(view: DataView) {
        val fc = FileChooser()
        fc.title = "Open Table from File"
        val f = fc.showOpenDialog(view.stage)
        if (f != null && f.extension == "csv") {
            Thread {
                try {
                    val a = TableArray.fromCSV(FileInputStream(f), true)
                    runOnFxThread {
                        view.spreadsheet.grid = a.toGrid()
                        view.spreadsheet.fixedRows.setAll(0)
                        view.stage.title = f.name
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun launchCommands() {
        val m = context.uiManager
        m.registerCommand("app.about", "About KnotBook", MDI_INFORMATION_OUTLINE.description,
                combo(KeyCode.F1)) { uiManager.view?.let { Splash.info(it.stage) } }
        m.registerCommand("nav.recent", "Open Recent", MDI_HISTORY.description,
                combo(KeyCode.R, control = true)) { }
        m.registerCommand("nav.file", "Open File", MDI_FOLDER_OUTLINE.description,
                combo(KeyCode.O, control = true)) { uiManager.view?.let { tableFromFile(it) } }
        m.registerCommand("window.close", "Close Window", MDI_CLOSE.description,
                combo(KeyCode.W, control = true)) { closeWindow() }
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
        ) { uiManager.view?.toggleFullScreen() }
        m.registerCommand("status.toggle", "Toggle Status Bar",
                null, combo(KeyCode.F10)) { uiManager.view?.toggleStatusBar() }
        m.registerCommand("window.create", "New Window", null,
                combo(KeyCode.N, control = true)) { newWindow() }
        m.registerCommand("test.python.editor", "Test Python Editor",
                MDI_LANGUAGE_PYTHON.description, null
        ) { context.createTextEditor().withSyntax("text/python").editable().show() }
        m.registerCommand("command.palette", "Command Palette", MDI_CONSOLE.description,
                combo(KeyCode.K, control = true)) { uiManager.showCommandsPalette() }
        m.registerCommand("app.license", "Open Source Licenses", null,
                null) { viewOpenSource() }
        m.registerCommand("app.exit", "Exit Application", null,
                null) { exitOK() }
        m.registerCommand("app.plugins", "Application Plugins", null,
                null) { viewPlugins() }
    }

    private fun launchCommands2() {
        val m = context.uiManager
        m.registerCommand("edit.undo", "Undo", MDI_UNDO.description, combo(KeyCode.Z, control = true)) {}
        m.registerCommand("edit.redo", "Redo", MDI_REDO.description,
                combo(KeyCode.Z, control = true, shift = true)) {}
        m.registerCommand("edit.cut", "Cut", MDI_CONTENT_CUT.description,
                combo(KeyCode.X, control = true)) {}
        m.registerCommand("edit.copy", "Copy", MDI_CONTENT_COPY.description,
                combo(KeyCode.C, control = true)) { uiManager.view?.copyTabDelimited() }
        m.registerCommand("edit.copy.special", "Copy Special", null,
                combo(KeyCode.C, control = true, shift = true)) {}
        m.registerCommand("edit.paste", "Paste", MDI_CONTENT_PASTE.description,
                combo(KeyCode.V, control = true)) {}
        m.registerCommand("edit.paste.special", "Paste Special", null,
                combo(KeyCode.V, control = true, shift = true)) {}
        m.registerCommand("select.all", "Select All", null, combo(KeyCode.A, control = true)) {}
        m.registerCommand("select.none", "Select None", null,
                combo(KeyCode.A, control = true, shift = true)) {}
        m.registerCommand("nav.find", "Find in Cells", null,
                combo(KeyCode.F, control = true)) {}
        m.registerCommand("view.zoom.in", "Zoom In", MDI_MAGNIFY_PLUS.description,
                combo(KeyCode.EQUALS, control = true)) {}
        m.registerCommand("view.zoom.out", "Zoom Out", MDI_MAGNIFY_MINUS.description,
                combo(KeyCode.MINUS, control = true)) {}
        m.registerCommand("view.zoom.reset", "Reset Zoom", null,
                combo(KeyCode.DIGIT0, control = true)) {}
    }

    fun exitOK() {
        Platform.runLater {
            dataServer.exit()
            Platform.exit()
            manager.exitOK()
        }
    }
}