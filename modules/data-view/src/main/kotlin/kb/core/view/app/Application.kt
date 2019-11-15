package kb.core.view.app

import javafx.application.Platform
import kb.core.view.DataView
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata
import kb.service.api.application.ApplicationService
import kb.service.api.application.ServiceManager
import kb.service.api.ui.UIManager

class Application : ApplicationService {

    private val metadata = ServiceMetadata("KnotBook DataView", "3.0")

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(
            manager: ServiceManager,
            context: ServiceContext,
            callback: Runnable
    ) {
        Platform.startup {
            Singleton.launch(manager, context)
            callback.run()
            DataView().show()
        }
    }

    override fun getUIManager(): UIManager {
        return Singleton.uiManager
    }
}