package kb.core.application

import java.io.File
import java.io.IOException

internal class UserFile : RegistryHandle {


    private val home = System.getProperty("user.home").replace(File.separatorChar, '/')

    private val registryFile = File(home, "knotbook.properties")

    private var loaded = ""

    override fun load(): String {
        return try {
            registryFile.createNewFile()
            loaded = registryFile.readText()
            loaded
        } catch (e: IOException) {
            ""
        }
    }

    override fun save(content: String) {
        try {
            if (content != loaded) {
                loaded = content
                registryFile.writeText(content)
            }
        } catch (e: IOException) {
        } catch (e: NullPointerException) {
        }
    }
}