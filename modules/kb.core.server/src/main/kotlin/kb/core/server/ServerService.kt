package kb.core.server

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata

class ServerService : Service {

    private val metadata = ServiceMetadata()
    private val server = Server()


    init {
        metadata.packageName = "kb.core.server"
        metadata.packageVersion = "3.0"
    }


    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {
//        server.server.start()
    }

    override fun terminate(): Boolean {
        server.server.stop(3)
        return false
    }
}