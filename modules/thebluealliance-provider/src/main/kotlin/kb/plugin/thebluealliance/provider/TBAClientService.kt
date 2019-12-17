package kb.plugin.thebluealliance.provider

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata

class TBAClientService : Service {

    private val metadata = ServiceMetadata(
            "The Blue Alliance Integration",
            "Fetches data tables from TBA"
    )

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {
        TBASingleton.launch(context)
    }
}