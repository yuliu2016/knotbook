package kb.core.context

import java.io.File

@Suppress("unused")
object Registry {

    private val map: MutableMap<String, String> = mutableMapOf()

    private val home = System.getProperty("user.home").replace(File.separatorChar, '/')
    private const val version = "RISE"

    private val registryFile = File(home, ".kb-registry.txt")

    @Suppress("SpellCheckingInspection")
    private val defaultProps = mapOf(
            "USERPATH" to home,
            "VERSION_PREFIX" to version,
            "SYSTEM_PATH" to "$home/$version/System"
    )

    fun parse(lines: List<String>) {
        map.clear()
        lines.map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains("=") }
                .map { it.split("=") }
                .associateTo(map) { it[0].trim() to it[1].trim() }
    }

    operator fun get(key: String): String? {
        return map[key]?.run {
            var replaced = this
            defaultProps.forEach {
                replaced = replaced.replace("{${it.key}}", it.value)
            }
            return@run replaced
        }
    }

    fun getHomeRelativePath(key: String): String? {
        return get(key)?.replace(home, "~")
    }

    operator fun set(key: String, value: String) {
        map[key] = value
    }

    fun join(): String {
        return map.entries.joinToString("\n") { "${it.key}=${it.value}" }
    }

    fun save() {
        registryFile.writeText(join())
    }

    fun load() {
        registryFile.createNewFile()
        val data = registryFile.readLines()
        parse(data)
    }
}