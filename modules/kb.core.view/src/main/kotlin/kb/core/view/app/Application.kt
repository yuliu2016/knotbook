package kb.core.view.app

import javafx.application.Platform
import kb.core.view.DataView
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata
import kb.service.api.application.ApplicationService
import kb.service.api.application.ServiceManager
import kotlin.concurrent.thread

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
        Singleton.zzNullableContext = context
        Singleton.zzNullableManager = manager
        Platform.startup {
            DataView().show()

            thread(isDaemon = true, name = "MemoryObserver") {
                while (true) {
                    val m = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0 / 1024.0).toInt() + 1
                    Platform.runLater {
                        Singleton.memoryUsed.value = "${m}M"
                    }
                    try {
                        Thread.sleep(5000)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }
    }

    override fun launchFast() {
    }
}