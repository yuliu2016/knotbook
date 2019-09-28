package kb.application

import kb.service.api.ServicePropListener
import kb.service.api.ServiceProps
import kb.service.api.application.ApplicationProps

class Registry(private val handle: RegistryHandle) : ApplicationProps {

    private val map: MutableMap<String, String> = mutableMapOf()
    private val listeners: MutableMap<String, ServicePropListener> = mutableMapOf()

    init {
        parse(handle.load().split("\n"))
    }

    private fun parse(lines: List<String>) {
        map.clear()
        lines.map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains("=") }
                .map { it.split("=") }
                .associateTo(map) { it[0].trim() to it[1].trim() }
    }

    operator fun get(key: String): String? {
        return map[key]
    }

    operator fun set(key: String, newValue: String) {
        val oldValue = map[key]
        map[key] = newValue
        if (oldValue == null || oldValue != newValue) {
            listeners[key]?.propertyChanged(oldValue, newValue)
        }
    }

    fun remove(key: String) {
        map.remove(key)
    }

    operator fun contains(key: String): Boolean {
        return key in map
    }

    private fun save() {
        handle.save(joinedText)
    }

    override fun getJoinedText(): String {
        return map.entries.joinToString("\n") { "${it.key}=${it.value}" }
    }

    override fun setInputText(inputText: String) {
        parse(inputText.split("\n"))
        save()
    }

    override fun hasProps(name: String): Boolean {
        return map.any { it.key.startsWith(name) }
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