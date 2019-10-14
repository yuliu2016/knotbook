@file:Suppress("unused")

package kb.core.fx

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination.*

@FXKtDSL
inline fun menuBar(builder: MenuBar.() -> Unit): MenuBar = MenuBar().apply(builder)

@FXKtDSL
fun MenuItem.name(name: String) {
    text = name
}

@FXKtDSL
fun MenuItem.action(handler: (ActionEvent) -> Unit) {
    onAction = EventHandler(handler)
}

@FXKtDSL
fun Menu.name(name: String) {
    text = name
}

typealias Combination = KeyCodeCombination

@FXKtDSL
fun MenuItem.shortcut(keyCode: KeyCode, control: Boolean = false, shift: Boolean = false, alt: Boolean = false) {
    accelerator = KeyCodeCombination(
            keyCode,
            if (control) SHORTCUT_DOWN else SHORTCUT_ANY,
            if (shift) SHIFT_DOWN else SHIFT_ANY,
            if (alt) ALT_DOWN else ALT_ANY
    )
}

@FXKtDSL
fun Modifier<MenuItem>.item(modifier: MenuItem.() -> Unit) {
    +MenuItem().apply(modifier)
}

@FXKtDSL
fun Modifier<MenuItem>.separator() {
    +SeparatorMenuItem()
}

@FXKtDSL
fun Modifier<MenuItem>.submenu(modifier: Menu.() -> Unit) {
    +Menu().apply(modifier)
}

@FXKtDSL
inline fun ContextMenu.modify(modifier: Modifier<MenuItem>.() -> Unit): ContextMenu {
    Modifier(items).apply(modifier)
    return this
}

@FXKtDSL
inline fun Menu.modify(modifier: Modifier<MenuItem>.() -> Unit) {
    Modifier(items).apply(modifier)
}

@FXKtDSL
inline fun MenuBar.modify(modifier: Modifier<Menu>.() -> Unit) {
    Modifier(menus).apply(modifier)
}

@FXKtDSL
fun Modifier<Menu>.menu(modifier: Menu.() -> Unit) {
    +Menu().apply(modifier)
}