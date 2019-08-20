package knotbook.core.fx

import javafx.application.Platform

@FXKtDSL
fun runLater(action: () -> Unit) {
    Platform.runLater(action)
}