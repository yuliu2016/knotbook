package kb.core.fx

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination.*
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

fun combo(
        keyCode: KeyCode,
        control: Boolean = false,
        alt: Boolean = false,
        shift: Boolean = false
): KeyCodeCombination = KeyCodeCombination(
        keyCode,
        if (control) SHORTCUT_DOWN else SHORTCUT_ANY,
        if (alt) ALT_DOWN else ALT_ANY,
        if (shift) SHIFT_DOWN else SHIFT_ANY
)