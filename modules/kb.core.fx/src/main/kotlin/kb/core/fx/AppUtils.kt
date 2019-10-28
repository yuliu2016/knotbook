package kb.core.fx

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Label

@Suppress("unused")
fun runOnFxThread(action: () -> Unit) {
    if (Platform.isFxApplicationThread()) {
        action()
    } else {
        Platform.runLater(action)
    }
}

@FXKtDSL
fun label(text: String) = Label(text)


@FXKtDSL
fun <T> List<T>.observable(): ObservableList<T> {
    return FXCollections.observableList(this)
}

@FXKtDSL
fun <T> MutableList<T>.addAll(vararg elements: T) {
    addAll(elements)
}