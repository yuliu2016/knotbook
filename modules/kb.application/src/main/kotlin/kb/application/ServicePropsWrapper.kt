package kb.application

import kb.service.api.ServicePropListener
import kb.service.api.ServiceProps

class ServicePropsWrapper(private val registry: Registry, private val name: String) : ServiceProps {

    private val String.wrapped get() = "$name/$this"

    override fun put(key: String, value: String) {
        registry[key.wrapped] = value
    }

    override fun get(key: String): String? {
        return registry[key.wrapped]
    }

    override fun remove(key: String) {
        registry.remove(key.wrapped)
    }

    override fun contains(key: String): Boolean {
        return key.wrapped in registry
    }

    override fun commit() {
    }

    override fun addListener(key: String, listener: ServicePropListener) {
    }

    override fun removeListener(key: String) {
    }
}