package kb.core.application

import java.io.File
import java.io.IOException

internal class UserFile : RegistryHandle {


    private val home = System.getProperty("user.home").replace(File.separatorChar, '/')

    private val registryFile = File(home, "knotbook.properties")


    override fun load(): String {
        return try {
            registryFile.createNewFile()
            registryFile.readText()
        } catch (e: IOException) {
            ""
        }
    }

    override fun save(content: String?) {
        try {
            registryFile.writeText(content!!)
        } catch (e: IOException) {

        } catch (e: NullPointerException) {

        }
    }
}