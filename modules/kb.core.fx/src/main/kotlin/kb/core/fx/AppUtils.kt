package kb.core.fx

import javafx.application.Platform

@Suppress("unused")
fun runOnFxThread(action: () -> Unit) {
    Platform.runLater(action)
}