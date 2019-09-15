package kb.application

import kb.service.api.ServicePropListener
import kb.service.api.ServiceProps
import kb.service.api.application.ApplicationProps
import java.io.File
import java.io.IOException

class Registry : ApplicationProps {

    private val map: MutableMap<String, String> = mutableMapOf()

    private val home = System.getProperty("user.home").replace(File.separatorChar, '/')

    private val registryFile = File(home, ".kb-registry.txt")


    private fun load() {
        try {
            registryFile.createNewFile()
            val data = registryFile.readLines()
            parse(data)
        } catch (e: IOException) {
        }
    }

    init {
        load()
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

    operator fun set(key: String, value: String) {
        map[key] = value
    }

    private fun save() {
        try {
            registryFile.writeText(joinedText)
        } catch (e: IOException) {
        }
    }

    override fun getJoinedText(): String {
        return map.entries.joinToString("\n") { "${it.key}=${it.value}" }
    }

    override fun setInputText(inputText: String) {
        parse(inputText.split("\n"))
        save()
    }

    override fun contains(name: String): Boolean {
        return map.any { it.key.startsWith(name) }
    }

    inner class ServicePropsWrapper(val name: String) : ServiceProps {
        override fun put(key: String, value: Boolean) {
            TODO("not implemented")
        }

        override fun put(key: String, value: Int) {
            TODO("not implemented")
        }

        override fun put(key: String, value: String) {
            TODO("not implemented")
        }

        override fun get(key: String): String {
            TODO("not implemented")
        }

        override fun get(key: String, defVal: String): String {
            TODO("not implemented")
        }

        override fun getBoolean(key: String, defVal: Boolean): Boolean {
            TODO("not implemented")
        }

        override fun getInt(key: String, defVal: Int): Int {
            TODO("not implemented")
        }

        override fun remove(key: String) {
            TODO("not implemented")
        }

        override fun contains(key: String): Boolean {
            TODO("not implemented")
        }

        override fun commit() {
            TODO("not implemented")
        }

        override fun addListener(key: String, listener: ServicePropListener) {
            TODO("not implemented")
        }
    }

    override fun getProps(name: String): ServiceProps {
        return ServicePropsWrapper(name)
    }
}