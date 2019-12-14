package kb.core.view.app

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.stage.FileChooser
import javafx.stage.Window
import kb.core.fx.combo
import kb.core.view.DataView
import kb.core.view.PresetCS
import kb.core.view.SortType
import kb.core.view.splash.Splash
import kb.service.api.ServiceContext
import kb.service.api.application.ServiceManager
import kb.service.api.array.Tables
import kb.service.api.json.JSONArrayWrapper
import kb.service.api.ui.OptionItem
import kb.service.api.ui.SearchBar
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign.MaterialDesign.*
import java.io.File
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
        uiManager.createTextEditor()
                .editable()
                .withSyntax("text/json")
                .withTitle("Settings")
                .withInitialText(manager.jsonConfig)
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
        uiManager.createTextEditor()
                .withTitle("JVM Properties (Read-Only)")
                .withSyntax("text/properties")
                .withInitialText(properties)
                .textWrapped()
                .show()
    }

    fun viewPlugins() {
        context.uiManager.showAlert("Plugins and Services",
                manager.services.joinToString("\n") { it.metadata.toString() })
    }

    fun viewJVMArgs() {
        context.uiManager.showAlert("JVM Args", manager.jvmArgs.joinToString("\n"))
    }

    fun viewOpenSource() {
        val t = Singleton::class.java
                .getResourceAsStream("/open_source.txt")
                .use { it.bufferedReader().readText() }
        uiManager.createTextEditor()
                .withTitle("Open Source Licences")
                .withInitialText(t)
                .textWrapped()
                .show()
    }

    fun viewThreads() {
        val traces = Thread.getAllStackTraces()
        val t = traces.keys.sortedByDescending { it.priority }.joinToString("\n") {
            val name = it.name + " ".repeat(26 - it.name.length)
            "$name Priority:${it.priority}  Daemon:${it.isDaemon}  Group:${it.threadGroup.name}"
        }
        uiManager.showAlertMonospace("Threads", t)
    }

    fun launch(manager: ServiceManager, context: ServiceContext, serviceLauncher: Runnable) {
        this.manager = manager
        this.context = context
        context.config["Build Version"] = manager.buildVersion
        context.config["Image Version"] = manager.imageVersion
        launchImpl()
        serviceLauncher.run()
        DataView().show()
        uiManager.showCommandsPalette()
    }

    private fun launchImpl() {
        Platform.setImplicitExit(false)
        val windows = Window.getWindows()
        windows.addListener(InvalidationListener { if (windows.isEmpty()) exitOK() })
        uiManager.startMemoryObserver()
        try {
            dataServer.bindAndStart()
        } catch (e: IOException) {
            Alert(Alert.AlertType.ERROR, "Application Already Started").showAndWait()
            manager.exitError()
        }
        if (context.config.optString("Theme") == "Light") {
            uiManager.themeProperty.set(DataUIManager.Theme.Light)
        }
        launchAppCommands()
        launchDataCommands()
    }

    fun closeWindow() {
        uiManager.view?.let { win ->
            uiManager.confirmOK("KnotBook DataView", "Are you sure you want to close this window?") {
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

    private var recent: JSONArrayWrapper? = null

    fun getRecent(): JSONArrayWrapper {
        if (recent == null) {
            recent = context.config.getJSONArray("Recent Files")
        }
        return recent!!
    }

    fun JSONArrayWrapper.toList1(): List<String> {
        val li = mutableListOf<String>()
        for (i in 0 until size) {
            li.add(get(i).toString())
        }
        return li
    }

    fun tableFromFile(view: DataView) {
        val fc = FileChooser()
        fc.title = "Open Table from File"
        val recent = getRecent()
        if (recent.isNotEmpty()) {
            val fp = recent.getString(0)
            fc.initialDirectory = File(fp).parentFile
        }

        val f = fc.showOpenDialog(view.stage)
        if (f != null && f.extension == "csv") {
            val p = f.absolutePath
            recent.remove(p)
            recent.add(0, p)
            thread(name = "CSV Loader") {
                try {
                    val a = Tables.fromCSV(FileInputStream(f), true)
                    context.dataSpace.newData(f.name, a)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getSavePath(view: DataView, ext: String): File? {
        val fc = FileChooser()
        fc.title = "Save File"
        val recent = getRecent()
        if (recent.isNotEmpty()) {
            val fp = recent.getString(0)
            fc.initialDirectory = File(fp).parentFile
        }
        fc.extensionFilters.add(FileChooser.ExtensionFilter(ext, "*.$ext"))
        return fc.showSaveDialog(view.stage)
    }

    val recentSearchBar = SearchBar()

    fun showRecent() {
        val recent = getRecent()
        val list1 = recent.toList1()
        recentSearchBar.setItems(list1.map { OptionItem(it, null, null, null, null) })

        uiManager.showOptionBar(recentSearchBar.toOptionBar())
    }

    fun showUpdates() {
        thread(name = "KnotBook Update Checker") {
            uiManager.showAlert("Check for Updates", manager.fetchUpdatedVersion())
        }
    }

    private fun launchAppCommands() {
        val m = context.uiManager
        m.registerCommand("app.about", "About KnotBook", MDI_INFORMATION_OUTLINE.description,
                combo(KeyCode.F1)) { Splash.info(uiManager.view?.stage, manager.buildVersion, appIcon) }
        m.registerCommand("nav.recent", "Open Recent", MDI_HISTORY.description,
                combo(KeyCode.R, control = true)) { showRecent() }
        m.registerCommand("file.open", "Open File", MDI_FOLDER_OUTLINE.description,
                combo(KeyCode.O, control = true)) { uiManager.view?.let { tableFromFile(it) } }
        m.registerCommand("window.close", "Close Window", MDI_CLOSE.description,
                combo(KeyCode.W, control = true)) { closeWindow() }
        m.registerCommand("app.config", "Settings",
                MDI_TUNE.description, combo(KeyCode.COMMA, control = true)) { editAppProperties() }
        m.registerCommand("theme.toggle", "Toggle Colour Scheme",
                MDI_COMPARE.description, combo(KeyCode.F3)) {
            uiManager.toggleTheme()
            context.config["Theme"] = uiManager.themeProperty.get().name
        }
        m.registerCommand("fullscreen.toggle", "Toggle Full Screen",
                MDI_ARROW_EXPAND.description, combo(KeyCode.F11)
        ) { uiManager.view?.toggleFullScreen() }
        m.registerCommand("window.create", "New Window", null,
                combo(KeyCode.N, control = true)) { newWindow().show() }
        m.registerCommand("test.python.editor", "Test Python Editor",
                MDI_LANGUAGE_PYTHON.description, null) {
            uiManager.createTextEditor().withSyntax("text/python")
                    .withTitle("Python Editor").editable().show()
        }
        m.registerCommand("command.palette", "Command Palette", MDI_CONSOLE.description,
                combo(KeyCode.K, control = true)) { uiManager.showCommandsPalette() }
        m.registerCommand("app.updates", "Check For Updates", MDI_UPDATE.description, null) {
            showUpdates()
        }
        m.registerCommand("app.license", "Open Source Licenses", null, null) { viewOpenSource() }
        m.registerCommand("plugins.list", "Plugins and Services", null, null) { viewPlugins() }
        m.registerCommand("app.exit", "JVM: Exit Application", null, null) { exitOK() }
        m.registerCommand("jvm.properties", "JVM: System Properties",
                MDI_COFFEE.description, null) { viewJVMProperties() }
        m.registerCommand("jvm.threads", "JVM: Show All Threads",
                null, combo(KeyCode.B, control = true, shift = true)) { viewThreads() }
        m.registerCommand("jvm.gc", "JVM: Run Memory Garbage Collection",
                MDI_DELETE_SWEEP.description, combo(KeyCode.B, control = true)) { Splash.gc(uiManager.view?.stage) }
        m.registerCommand("jvm.args", "JVM: Arguments", null, null) { viewJVMArgs() }
    }

    private fun launchDataCommands() {
        registerForView("status.toggle", "Toggle Status Bar",
                null, combo(KeyCode.F10)) { it.toggleStatusBar() }
        registerForView("edit.copy", "Copy", MDI_CONTENT_COPY,
                combo(KeyCode.C, control = true)) { it.copyDelimited('\t') }
        registerForView("file.save.csv", "Save as CSV", MDI_CONTENT_SAVE,
                combo(KeyCode.S, control = true)) { it.saveCSV() }
        registerForView("file.save.zip", "Save as Native Zip", MDI_CONTENT_SAVE,
                combo(KeyCode.S, control = true, shift = true)) { it.saveZip() }
        registerForView("select.all", "Select All", null,
                combo(KeyCode.A, control = true)) { it.selectAll() }
        registerForView("select.none", "Select None", null,
                combo(KeyCode.A, control = true, shift = true)) { it.selectNone() }

        registerForView("view.zoom.in", "Zoom In", MDI_MAGNIFY_PLUS, null
        ) { it.spreadsheet.incrementZoom() }
        registerForView("view.zoom.out", "Zoom Out", MDI_MAGNIFY_MINUS, null
        ) { it.spreadsheet.decrementZoom() }
        registerForView("view.zoom.reset", "Reset Zoom", null, null
        ) { it.spreadsheet.zoomFactor = 1.0 }
        registerForView("data.view.find", "Find in Cells", null,
                combo(KeyCode.F, control = true)) { it.startFind() }
        registerForView("cs.clear", "Clear Colour Scales",
                null, combo(KeyCode.DIGIT0, alt = true)) { it.clearColourScale() }

        registerForView("cs.up.1", "Add Ascending Colour Scale: Green",
                null, combo(KeyCode.DIGIT1, alt = true))
        { it.addColourScale(SortType.Ascending, PresetCS.green) }
        registerForView("cs.up.2", "Add Ascending Colour Scale: Red",
                null, combo(KeyCode.DIGIT2, alt = true))
        { it.addColourScale(SortType.Ascending, PresetCS.red) }
        registerForView("cs.up.3", "Add Ascending Colour Scale: Orange",
                null, combo(KeyCode.DIGIT3, alt = true))
        { it.addColourScale(SortType.Ascending, PresetCS.orange) }
        registerForView("cs.up.4", "Add Ascending Colour Scale: Blue",
                null, combo(KeyCode.DIGIT4, alt = true))
        { it.addColourScale(SortType.Ascending, PresetCS.blue) }

        registerForView("cs.down.9", "Add Descending Colour Scale: Green",
                null, combo(KeyCode.DIGIT9, alt = true))
        { it.addColourScale(SortType.Descending, PresetCS.green) }
        registerForView("cs.down.8", "Add Descending Colour Scale: Red",
                null, combo(KeyCode.DIGIT8, alt = true))
        { it.addColourScale(SortType.Descending, PresetCS.red) }
        registerForView("cs.down.7", "Add Descending Colour Scale: Orange",
                null, combo(KeyCode.DIGIT7, alt = true))
        { it.addColourScale(SortType.Descending, PresetCS.orange) }
        registerForView("cs.down.6", "Add Ascending Colour Scale: Blue",
                null, combo(KeyCode.DIGIT6, alt = true))
        { it.addColourScale(SortType.Descending, PresetCS.blue) }

        registerForView("sort.ascending", "Set Ascending Sort",
                MDI_SORT_ASCENDING, combo(KeyCode.OPEN_BRACKET, control = true))
        { it.setSort(SortType.Ascending) }
        registerForView("sort.descending", "Set Descending Sort",
                MDI_SORT_DESCENDING, combo(KeyCode.CLOSE_BRACKET, control = true))
        { it.setSort(SortType.Descending) }

        registerForView("sort.ascending.add", "Add Ascending Sort",
                MDI_SORT_ASCENDING, combo(KeyCode.OPEN_BRACKET, control = true, shift = true))
        { it.addSort(SortType.Ascending) }
        registerForView("sort.descending.add", "Add Descending Sort",
                MDI_SORT_DESCENDING, combo(KeyCode.CLOSE_BRACKET, control = true, shift = true))
        { it.addSort(SortType.Descending) }

        registerForView("sort.clear", "Clear Sort", null, combo(KeyCode.BACK_SLASH, control = true))
        { it.clearSort() }

        registerForView("table.print", "Print Table to Standard Output", null, null)
        { it.array?.let { data -> println(data) } }

        registerForView("columns.hide", "Select Columns as Table", MDI_TABLE_COLUMN_WIDTH,
                combo(KeyCode.T, control = true, shift = true)) { it.selectColumns() }
        registerForView("filter.in", "Filter By as Table", MDI_FILTER,
                combo(KeyCode.Y, control = true)) { it.filterBy() }
        registerForView("filter.out", "Filter Out as Table", MDI_FILTER_OUTLINE,
                combo(KeyCode.U, control = true)) { it.filterOut() }
    }

    fun registerForView(id: String, name: String, icon: Ikon?, combo: KeyCombination?, func: (DataView) -> Unit) {
        uiManager.registerCommand(id, name, icon?.description, combo) { uiManager.view?.let(func) }
    }

    fun exitOK() {
        Platform.runLater {
            dataServer.exit()
            Platform.exit()
            manager.exitOK()
        }
    }
}