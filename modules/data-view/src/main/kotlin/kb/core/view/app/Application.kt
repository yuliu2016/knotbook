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
            pluginSetup: Runnable
    ) {
        if (context.config.optBoolean("Disable DPI Scaling")) {
            System.setProperty("prism.allowhidpi", "false")
        }
        Platform.startup {
            Singleton.launch(manager, context)
            pluginSetup.run()
            DataView().show()
        }
    }

    override fun getUIManager(): UIManager {
        return Singleton.uiManager
    }
}