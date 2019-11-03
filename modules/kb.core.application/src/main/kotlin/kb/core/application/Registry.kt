package kb.core.application

import kb.service.api.ServicePropListener
import kb.service.api.ServiceProps
import kb.service.api.application.ApplicationProps
import java.io.ByteArrayOutputStream
import java.util.*

internal class Registry(private val handle: RegistryHandle) : ApplicationProps {

    private val listeners: MutableMap<String, ServicePropListener> = mutableMapOf()

    private val props = Properties()

    private var changed = false

    init {
        handle.input().use { props.load(it) }
    }

    operator fun get(key: String): String? {
        return props.getProperty(key)
    }

    operator fun set(key: String, newValue: String) {
        val oldValue = props[key]
        props[key] = newValue
        changed = true
        if (oldValue == null || oldValue != newValue) {
            listeners[key]?.propertyChanged(oldValue.toString(), newValue)
        }
    }

    fun remove(key: String) {
        changed = true
        props.remove(key)
    }

    operator fun contains(key: String): Boolean {
        return key in props
    }

    fun save() {
        if (changed) {
            handle.output().use { props.store(it, null) }
        }
    }

    override fun getJoinedText(): String {
        val bout = ByteArrayOutputStream()
        props.store(bout, null)
        return bout.toString(Charsets.UTF_8)
    }

    override fun setInputText(inputText: String) {
        props.load(inputText.byteInputStream())
        changed = true
        save()
    }

    override fun hasProps(name: String): Boolean {
        return props.keys.any { it.toString().startsWith(name) }
    }

    override fun getProps(name: String): ServiceProps {
        return ServicePropsWrapper(this, name)
    }

    fun addListener(key: String, listener: ServicePropListener) {
        listeners[key] = listener
    }

    fun removeListener(key: String) {
        listeners.remove(key)
    }
}