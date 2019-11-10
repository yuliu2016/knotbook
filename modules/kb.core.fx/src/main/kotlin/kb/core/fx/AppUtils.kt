package kb.core.fx

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox

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

@FXKtDSL
fun Node?.centered(width: Int): HBox = HBox().also {
    it.prefWidth = width.toDouble()
    if (this != null) {
        it.children.add(this)
        it.alignment = Pos.CENTER
    }
}