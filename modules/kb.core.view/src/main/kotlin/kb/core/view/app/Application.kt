package kb.core.view.app

import javafx.application.Platform
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata
import kb.service.api.application.ApplicationService
import kb.service.api.application.ServiceManager
import kb.service.api.ui.UIManager

class Application : ApplicationService {

    private val metadata = ServiceMetadata()

    init {
        metadata.packageName = "KnotBook DataView"
        metadata.packageVersion = "3.0"
    }

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(manager: ServiceManager, context: ServiceContext, callback: Runnable) {
        Platform.startup {
            Singleton.launch(manager, context)
            callback.run()
        }
    }

    override fun getUIManager(): UIManager {
        return Singleton.uiManager
    }
}