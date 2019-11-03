package kb.core.view.app

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleStringProperty
import javafx.stage.Window
import kb.core.view.DataView
import kb.core.view.server.Server
import kb.service.api.ServiceContext
import kb.service.api.application.ServiceManager
import kotlin.concurrent.thread

@Suppress("unused", "MemberVisibilityCanBePrivate")
internal object Singleton {
    val memoryUsed = SimpleStringProperty()
    val serverState = SimpleStringProperty()

    private var nullableManager: ServiceManager? = null
    private var nullableContext: ServiceContext? = null

    val manager get() = nullableManager!!
    val context get() = nullableContext!!

    var focusedWindow: WindowBase? = null

    val apiServer = Server()

    fun editAppProperties() {
        context.createTextEditor()
                .editable()
                .withSyntax("text/properties")
                .withTitle("Application Properties")
                .withInitialText(manager.props.joinedText)
                .addAction("Save Changes") { changed, finalText ->
                    if (changed) {
                        manager.props.setInputText(finalText)
                    }
                }
                .show()
    }

    fun viewAppProperties() {
        context.createTextEditor()
                .withSyntax("text/properties")
                .withTitle("Application Properties (Read Only)")
                .withInitialText(manager.props.joinedText)
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
                        Singleton.memoryUsed.value = "${memoryUsed}M"
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
        if (nullableContext == null && nullableManager == null) {
            nullableContext = context
            nullableManager = manager
            apiServer.stateCallback = { Platform.runLater { serverState.set(it) } }
            apiServer.bindAndStart()
            try {
                Platform.startup(this::launchImpl)
            } catch (e: IllegalStateException) {
                Platform.runLater(this::launchImpl)
            }
        }
    }

    private fun launchImpl() {
        startMemoryObserver()
        Platform.setImplicitExit(false)
        val windows = Window.getWindows()
        windows.addListener(InvalidationListener { if (windows.isEmpty()) exit() })
        DataView().show()
    }

    fun exit() {
        Platform.runLater {
            apiServer.exit()
            Platform.exit()
            manager.exit()
        }
    }
}