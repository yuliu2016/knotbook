@file:Suppress("unused")

package knotbook.core.fx

import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

@FXKtDSL
fun Pane.add(node: Node) {
    children.add(node)
}

@FXKtDSL
fun Pane.addAll(vararg node: Node) {
    children.addAll(node)
}

@FXKtDSL
fun HBox.hspace() = add(hbox {
    HBox.setHgrow(this, Priority.ALWAYS)
})

@FXKtDSL
fun VBox.vspace() = add(vbox {
    VBox.setVgrow(this, Priority.ALWAYS)
})

@FXKtDSL
fun <T : Node> T.hgrow(): T {
    HBox.setHgrow(this, Priority.ALWAYS)
    return this
}

@FXKtDSL
fun <T : Node> T.vgrow(): T {
    VBox.setVgrow(this, Priority.ALWAYS)
    return this
}

@FXKtDSL
fun SplitPane.addFixed(vararg node: Node) {
    node.forEach { SplitPane.setResizableWithParent(it, false) }
    items.addAll(node)
}

@FXKtDSL
inline fun Pane.modify(modifier: Modifier<Node>.() -> Unit) {
    Modifier(children).apply(modifier)
}