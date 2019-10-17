package kb.core.view.app

import javafx.beans.property.SimpleStringProperty
import kb.service.api.ServiceContext
import kb.service.api.application.ServiceManager

@Suppress("unused")
internal object Singleton {
    val memoryUsed = SimpleStringProperty()

    var zzNullableManager: ServiceManager? = null
    var zzNullableContext: ServiceContext? = null

    val manager by lazy { zzNullableManager!! }
    val context by lazy { zzNullableContext!! }

    fun editAppProperties() {
        context.textEditor()
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
        context.textEditor()
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
        context.textEditor().apply {
            title = "JVM Properties (Read-Only)"
            syntax = "text/properties"
            setInitialText(properties)
            show()
        }
    }

    fun viewPlugins() {
        val t = manager.services.joinToString("\n") {
            it.metadata.run { "$packageName => $packageVersion" }
        }
        context.textEditor().apply {
            title = "Plugins and Services"
            setInitialText(t)
            show()
        }
    }

    fun viewOpenSource() {
        val t = Singleton::class.java.getResourceAsStream("/open_source.txt").reader().readText()
        context.textEditor()
                .withTitle("Open Source Licences")
                .withInitialText(t)
                .show()
    }
}