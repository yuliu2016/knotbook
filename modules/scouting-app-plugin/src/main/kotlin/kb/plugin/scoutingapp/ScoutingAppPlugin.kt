package kb.plugin.scoutingapp

import javafx.scene.input.KeyCode
import kb.core.fx.combo
import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata

class ScoutingAppPlugin : Service {

    private val metadata = ServiceMetadata("Scouting App Plugin", "1.0")

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {
        val config = context.config

        config["Event"] = "2020onbar"
        config["Year"] = 2020
        config["Raw Data Location"] = "~/Desktop/RawData/{event}"

        context.uiManager.registerCommand("scouting.scanner", "Scouting App: Launch Scanner",
                "mdi-qrcode", combo(KeyCode.I, control = true, shift = true)) {}
        context.uiManager.registerCommand("scouting.event.set", "Scouting App: Set Event",
                null) {}
    }
}