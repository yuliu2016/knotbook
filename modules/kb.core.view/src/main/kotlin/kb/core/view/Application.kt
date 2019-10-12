package kb.core.view

import javafx.application.Platform
import kb.service.api.ServiceMetadata
import kb.service.api.application.ApplicationService
import kb.service.api.application.PrivilagedContext

class Application : ApplicationService {

    private val metadata = ServiceMetadata()

    init {
        metadata.packageName = "kb.core.view"
        metadata.packageVersion = "3.0"
    }

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: PrivilagedContext) {
    }

    override fun launchFast() {
        Platform.startup {
            AppView().show()
        }
    }

}