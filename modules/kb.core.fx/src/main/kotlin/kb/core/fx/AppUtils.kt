package kb.core.fx

import javafx.application.Platform

@Suppress("unused")
fun appRunLater(action: () -> Unit) {
    Platform.runLater(action)
}