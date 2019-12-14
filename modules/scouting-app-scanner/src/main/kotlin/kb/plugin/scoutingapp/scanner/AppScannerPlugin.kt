package kb.plugin.scoutingapp.scanner

import javafx.scene.input.KeyCode
import kb.core.fx.combo
import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata

class AppScannerPlugin : Service {

    private val metadata = ServiceMetadata(
            "Scouting App Scanner",
            "Scans QR Codes with the WebCam"
    )

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {

        context.uiManager.registerCommand("scouting.scanner", "Scouting App: Launch Scanner",
                "mdi-qrcode", combo(KeyCode.S, control = true, alt = true)) {
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