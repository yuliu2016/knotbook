package kb.core.view.app

import javafx.application.Platform
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata
import kb.service.api.application.ApplicationService
import kb.service.api.application.ServiceManager
import kb.service.api.data.DataSpace
import kb.service.api.ui.UIManager

class Application : ApplicationService {

    private val metadata = ServiceMetadata(
            "KnotBook DataView",
            ""
    )

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(
            manager: ServiceManager,
            context: ServiceContext,
            serviceLauncher: Runnable
    ) {
        if (context.config.optBoolean("Disable DPI Scaling")) {
            System.setProperty("prism.allowhidpi", "false")
        }
        Platform.startup { Singleton.launch(manager, context, serviceLauncher) }
    }

    override fun getUIManager(): UIManager {
        return Singleton.uiManager
    }

    override fun getDataSpace(): DataSpace {
        return Singleton.dataSpace
    }
}