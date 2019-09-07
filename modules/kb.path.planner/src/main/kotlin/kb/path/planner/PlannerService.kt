package kb.path.planner

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata

class PlannerService : Service {

    private val metadata = ServiceMetadata()

    init {
        metadata.isDataProvider = false
        metadata.isUserInterface = true
        metadata.packageName = "kb.path.planner"
        metadata.packageVersion = "kb"
    }

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {
    }
}