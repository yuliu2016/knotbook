package kb.core.application

import java.io.*

internal class UserFile : RegistryHandle {

    private val home = System.getProperty("user.home").replace(File.separatorChar, '/')

    private val registryFile = File(home, "knotbook.properties")

    override fun input(): InputStream {
        return FileInputStream(registryFile)
    }

    override fun output(): OutputStream {
        return FileOutputStream(registryFile)
    }
}