package kb.core.icon

import javafx.scene.control.MenuItem
import org.kordamp.ikonli.Ikon

@DslMarker
annotation class IconDSL

@IconDSL
fun fontIcon(ic: Ikon, size: Int): FontIcon {
    return FontIcon.of(ic, size)
}

@IconDSL
fun MenuItem.icon(icon: Ikon, iconSize: Int) {
    graphic = fontIcon(icon, iconSize)
    // this is so that icons are moved away from the text
    graphic.translateX = -2.0
}
