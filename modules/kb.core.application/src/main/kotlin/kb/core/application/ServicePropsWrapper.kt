package kb.core.application

import kb.service.api.ServicePropListener
import kb.service.api.ServiceProps

class ServicePropsWrapper(private val registry: Registry, private val name: String) : ServiceProps {

    private fun String.wrap(): String {
        return "$name/$this"
    }

    private fun isValid(key: String): Boolean {
        return key.isNotEmpty() and key[0].isLetter() and
                key.all { it.isLetterOrDigit() || it == '.' }
    }

    override fun put(key: String, value: String) {
        if (isValid(key)) {
            registry[key.wrap()] = value
        }
    }

    override fun get(key: String): String? {
        return registry[key.wrap()]
    }

    override fun remove(key: String) {
        registry.remove(key.wrap())
    }

    override fun contains(key: String): Boolean {
        return key.wrap() in registry
    }

    override fun addListener(key: String, listener: ServicePropListener) {
        if (isValid(key)) {
            registry.addListener(key.wrap(), listener)
        }
    }

    override fun removeListener(key: String) {
        registry.removeListener(key.wrap())
    }
}