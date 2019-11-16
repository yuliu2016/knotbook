package kb.plugin.appscanner

import javafx.scene.input.KeyCode
import kb.core.fx.combo
import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata

class AppScannerPlugin : Service {

    private val metadata = ServiceMetadata("Scouting App Scanner", "1.0")

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {

        context.uiManager.registerCommand("scouting.scanner", "Scouting App: Launch Scanner",
                "mdi-qrcode", combo(KeyCode.I, control = true, shift = true)) {
            ScannerScreen().show()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ScannerScreen().show()
        }
    }
}