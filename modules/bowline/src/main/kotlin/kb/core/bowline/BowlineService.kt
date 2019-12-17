package kb.core.bowline

import javafx.scene.Scene
import javafx.stage.Stage
import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata

class BowlineService : Service {

    private val meta = ServiceMetadata("BowlineTableView", "Test Plugin")

    override fun getMetadata(): ServiceMetadata {
        return meta
    }

    override fun launch(context: ServiceContext) {
        context.uiManager.registerCommand("bowline.test", "Try BowlineTableView",
                "mdi-bowl", null) {
            val stage = Stage()
            stage.scene = Scene(BowlineTable())
            stage.scene.stylesheets.add("/bowline.css")
            stage.title = "Try BowlineTableView"
            stage.width = 800.0
            stage.height = 600.0
            stage.show()
        }
    }

}