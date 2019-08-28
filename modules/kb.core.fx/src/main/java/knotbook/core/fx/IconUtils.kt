package knotbook.core.fx

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import knotbook.core.icon.FontIcon
import org.kordamp.ikonli.Ikon

@FXKtDSL
fun fontIcon(ic: Ikon, size: Int): FontIcon {
    return FontIcon.of(ic, size)
}

@Suppress("unused")
@FXKtDSL
fun FontIcon.centerIn(width: Int): HBox = hbox {
    add(this@centerIn)
    prefWidth = width.toDouble()
    alignment = Pos.CENTER
}