package kb.core.view.app

import javafx.beans.property.SimpleStringProperty
import kb.service.api.application.PrivilegedContext

@Suppress("unused")
internal object Singleton {
    val memoryUsed = SimpleStringProperty()

    var zzNullableContext: PrivilegedContext? = null

    val context by lazy { zzNullableContext!! }

    fun editAppProperties() {
        context.createTextEditor()
                .editable()
                .withSyntax("text/properties")
                .withTitle("Application Properties")
                .withInitialText(context.props.joinedText)
                .addAction("Save Changes") { changed, finalText ->
                    if (changed) {
                        context.props.setInputText(finalText)
                    }
                }
                .show()
    }

    fun viewAppProperties() {
        context.createTextEditor()
                .withSyntax("text/properties")
                .withTitle("Application Properties (Read Only)")
                .withInitialText(context.props.joinedText)
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
        context.createTextEditor().apply {
            title = "JVM Properties (Read-Only)"
            syntax = "text/properties"
            setInitialText(properties)
            show()
        }
    }

    fun viewPlugins() {
        val t = context.services.joinToString("\n") {
            it.metadata.run { "$packageName => $packageVersion" }
        }
        context.createTextEditor().apply {
            title = "Plugins and Services"
            setInitialText(t)
            show()
        }
    }

    fun viewOpenSource() {
        val t = Singleton::class.java.getResourceAsStream("/open_source.txt").reader().readText()
        context.createTextEditor()
                .withTitle("Open Source Licences")
                .withInitialText(t)
                .show()
    }
}