package knotbook.core.fx

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import knotbook.core.fx.FXKtDSL
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.javafx.FontIcon

@FXKtDSL
fun fontIcon(ic: Ikon, size: Int): FontIcon {
    return FontIcon.of(ic, size)
}

@FXKtDSL
fun FontIcon.centerIn(width: Int): HBox = hbox {
    add(this@centerIn)
    prefWidth = width.toDouble()
    alignment = Pos.CENTER
}