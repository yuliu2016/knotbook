package knotbook.core.fx

import javafx.application.Platform

@Suppress("unused")
@FXKtDSL
fun runLater(action: () -> Unit) {
    Platform.runLater(action)
}