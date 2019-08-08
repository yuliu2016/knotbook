@file:Suppress("unused", "SpellCheckingInspection", "NOTHING_TO_INLINE")

package knotbook.core.fx

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.*

// CREATORS

@FXKtDSL
inline fun hbox(builder: HBox.() -> Unit): HBox = HBox().apply(builder)

@FXKtDSL
inline fun vbox(builder: VBox.() -> Unit): VBox = VBox().apply(builder)

@FXKtDSL
fun HBox.hspace() = add(hbox {
    HBox.setHgrow(this, Priority.ALWAYS)
})

@FXKtDSL
fun VBox.vspace() = add(vbox {
    VBox.setVgrow(this, Priority.ALWAYS)
})

@FXKtDSL
fun <T: Node> T.hgrow(): T {
    HBox.setHgrow(this, Priority.ALWAYS)
    return this
}

@FXKtDSL
fun <T: Node> T.vgrow(): T {
    VBox.setVgrow(this, Priority.ALWAYS)
    return this
}

@FXKtDSL
inline fun textField(builder: TextField.() -> Unit): TextField = TextField().apply(builder)

@FXKtDSL
inline fun splitPane(builder: SplitPane.() -> Unit): SplitPane = SplitPane().apply(builder)

@FXKtDSL
fun SplitPane.addFixed(vararg  node: Node) {
    node.forEach { SplitPane.setResizableWithParent(it, false) }
    items.addAll(node)
}

@FXKtDSL
inline fun Pane.modify(modifier: Modifier<Node>.() -> Unit) {
    Modifier(children).apply(modifier)
}

// PROPERTY SETTERS

@FXKtDSL
fun Node.styleClass(sc: String) {
    styleClass.add(sc)
    properties["FXKtStyleClass"] = sc
}

@FXKtDSL
fun Node.noStyleClass() {
    styleClass.remove(properties["FXKtStyleClass"] ?: "")
}

@FXKtDSL
fun HBox.align(pos: Pos) {
    alignment = pos
}

@FXKtDSL
fun VBox.align(pos: Pos) {
    alignment = pos
}

@FXKtDSL
fun Pane.add(node: Node) {
    children.add(node)
}

@FXKtDSL
fun Region.height(height: Double) {
    prefHeight = height
}

@FXKtDSL
fun <T> List<T>.observable(): ObservableList<T> {
    return FXCollections.observableList(this)
}