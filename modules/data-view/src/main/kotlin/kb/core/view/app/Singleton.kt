package kb.core.view.app

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.stage.FileChooser
import javafx.stage.Window
import kb.core.fx.combo
import kb.core.view.DataView
import kb.core.view.server.Server
import kb.core.view.splash.Splash
import kb.service.api.ServiceContext
import kb.service.api.application.ServiceManager
import kb.service.api.array.TableArray
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.io.FileInputStream
import java.io.IOException
import kotlin.concurrent.thread

/**
 * Singleton object representing the application
 *
 * Runs on JavaFX application thread
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal object Singleton {

    // Application interfaces
    lateinit var manager: ServiceManager
    lateinit var context: ServiceContext

    val dataServer = Server()
    val uiManager = DataUIManager()
    val dataSpace = TableSpace()
    val appIcon = Image(DataView::class.java.getResourceAsStream("/icon.png"))

    fun editAppProperties() {
        context.createTextEditor()
                .editable()
                .withSyntax("text/json")
                .withTitle("Settings")
                .withInitialText(manager.jsonConfig)
                .withDarkTheme(uiManager.isDarkTheme())
                .addAction("Save Changes") { changed, finalText ->
                    if (changed) {
                        manager.jsonConfig = finalText
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
        context.uiManager.showAlert("Plugins and Services", manager.services.joinToString("\n"))
    }

    fun viewJVMArgs() {
        context.uiManager.showAlert("JVM Args", manager.jvmArgs.joinToString("\n"))
    }

    fun viewOpenSource() {
        val t = Singleton::class.java
                .getResourceAsStream("/open_source.txt")
                .use { it.bufferedReader().readText() }
        context.createTextEditor()
                .withTitle("Open Source Licences")
                .withInitialText(t)
                .withDarkTheme(uiManager.isDarkTheme())
                .textWrapped()
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

    fun launch(manager: ServiceManager, context: ServiceContext, serviceLauncher: Runnable) {
        this.manager = manager
        this.context = context
        launchImpl()
        serviceLauncher.run()
        DataView().show()
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
            manager.exitError()
        }
        launchCommands()
        launchCommands2()
    }

    private fun closeWindow() {
        uiManager.view?.let { win ->
            uiManager.confirmOK("Confirming", "Close this window?") {
                win.stage.close()
            }
        }
    }

    fun newWindow(): DataView {
        val dv = DataView()
        uiManager.view?.let { win ->
            dv.stage.x = win.stage.x + 48.0
            dv.stage.y = win.stage.y + 36.0
        }
        return dv
    }

    fun tableFromFile(view: DataView) {
        val fc = FileChooser()
        fc.title = "Open Table from File"
        val f = fc.showOpenDialog(view.stage)
        if (f != null && f.extension == "csv") {
            Thread {
                try {
                    val a = TableArray.fromCSV(FileInputStream(f), true)
                    context.dataSpace.newData(f.name, a)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun launchCommands() {
        val m = context.uiManager
        m.registerCommand("app.about", "About KnotBook", MDI_INFORMATION_OUTLINE.description,
                combo(KeyCode.F1)) { Splash.info(uiManager.view?.stage, manager.buildVersion, appIcon) }
        m.registerCommand("nav.recent", "Open Recent", MDI_HISTORY.description,
                combo(KeyCode.R, control = true)) { }
        m.registerCommand("file.open", "Open File", MDI_FOLDER_OUTLINE.description,
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
                combo(KeyCode.N, control = true)) { newWindow().show() }
        m.registerCommand("test.python.editor", "Test Python Editor",
                MDI_LANGUAGE_PYTHON.description, null) {
            context.createTextEditor().withSyntax("text/python")
                    .withTitle("Python Editor").editable().show()
        }
        m.registerCommand("command.palette", "Command Palette", MDI_CONSOLE.description,
                combo(KeyCode.K, control = true)) { uiManager.showCommandsPalette() }
        m.registerCommand("app.license", "Open Source Licenses", null, null) { viewOpenSource() }
        m.registerCommand("app.exit", "Exit Application", null, null) { exitOK() }
        m.registerCommand("plugins.list", "Plugins and Services", null, null) { viewPlugins() }
        m.registerCommand("jvm.args", "JVM Arguments", null, null) { viewJVMArgs()}
    }

    private fun launchCommands2() {
        val m = context.uiManager
        m.registerCommand("edit.copy", "Copy", MDI_CONTENT_COPY.description,
                combo(KeyCode.C, control = true)) { uiManager.view?.copyDelimited('\t') }
        m.registerCommand("select.all", "Select All", null,
                combo(KeyCode.A, control = true)) { uiManager.view?.selectAll() }
        m.registerCommand("select.none", "Select None", null,
                combo(KeyCode.A, control = true, shift = true)) { uiManager.view?.selectNone() }

        m.registerCommand("view.zoom.in", "Zoom In", MDI_MAGNIFY_PLUS.description, null
        ) { uiManager.view?.spreadsheet?.incrementZoom() }
        m.registerCommand("view.zoom.out", "Zoom Out", MDI_MAGNIFY_MINUS.description, null
        ) { uiManager.view?.spreadsheet?.decrementZoom() }
        m.registerCommand("view.zoom.reset", "Reset Zoom", null, null
        ) { uiManager.view?.spreadsheet?.zoomFactor = 1.0 }
        m.registerCommand("table.find", "Find in Cells", null,
                combo(KeyCode.F, control = true)) {}
    }

    fun exitOK() {
        Platform.runLater {
            dataServer.exit()
            Platform.exit()
            manager.exitOK()
        }
    }
}