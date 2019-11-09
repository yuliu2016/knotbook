package kb.core.view.app

import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata
import kb.service.api.application.ApplicationService
import kb.service.api.application.ServiceManager
import kb.service.api.ui.UIManager

class Application : ApplicationService {

    private val metadata = ServiceMetadata()

    init {
        metadata.packageName = "kb.core.view"
        metadata.packageVersion = "3.0"
    }

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(manager: ServiceManager, context: ServiceContext) {
        Singleton.launch(manager, context)
    }

    override fun getUIManager(): UIManager {
        return Singleton.uiManager
    }
}