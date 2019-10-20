package kb.tba.extension

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata

class TBAClientService : Service {

    private val metadata = ServiceMetadata()

    init {
        metadata.packageName = "kb.tba.client"
        metadata.packageVersion = "3.0"
    }

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {
    }
}