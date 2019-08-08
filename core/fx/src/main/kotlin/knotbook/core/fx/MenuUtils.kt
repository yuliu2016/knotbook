@file:Suppress("unused")

package knotbook.core.fx

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCodeCombination
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.javafx.FontIcon

typealias Combo = KeyCodeCombination

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

@FXKtDSL
fun Menu.icon(icon: Ikon, iconSize: Int) {
    graphic = fontIcon(icon, iconSize)
}

@FXKtDSL
fun MenuItem.icon(icon: Ikon, iconSize: Int) {
    graphic = fontIcon(icon, iconSize)
}

@FXKtDSL
fun Modifier<MenuItem>.item(modifier: MenuItem.() -> Unit) {
    +MenuItem().apply(modifier)
}

@FXKtDSL
fun Modifier<MenuItem>.submenu(modifier: Menu.() -> Unit) {
    +Menu().apply(modifier)
}