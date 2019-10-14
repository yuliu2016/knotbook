package kb.core.view

import javafx.application.Platform
import kb.service.api.ServiceMetadata
import kb.service.api.application.ApplicationService
import kb.service.api.application.PrivilegedContext
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

    override fun launch(context: PrivilegedContext) {
        Singleton.zzNullableContext = context
        Platform.startup {
            AppView().show()

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