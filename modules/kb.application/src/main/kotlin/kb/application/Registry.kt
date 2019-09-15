package kb.application

import kb.service.api.ServiceProps
import kb.service.api.application.ApplicationProps
import java.io.File

class Registry : ApplicationProps {

    override fun getJoinedText(): String {
        return map.entries.joinToString("\n") { "${it.key}=${it.value}" }
    }

    override fun setInputText(inputText: String) {
        parse(inputText.split("\n"))
        save()
    }

    override fun getProps(name: String): ServiceProps {
        TODO("not implemented")
    }

    override fun contains(name: String): Boolean {
        return map.any { it.key.startsWith(name) }
    }

    private val map: MutableMap<String, String> = mutableMapOf()

    private val home = System.getProperty("user.home").replace(File.separatorChar, '/')

    private val registryFile = File(home, ".kb-registry.txt")


    private fun load() {
        registryFile.createNewFile()
        val data = registryFile.readLines()
        parse(data)
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
        registryFile.writeText(joinedText)
    }

}