package knotbook.core.table

import javafx.event.EventHandler
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color

class Knotable : Control() {

    private val ks = KnotableSkin(this)

    init {
        background = Background(BackgroundFill(Color.WHITE, null, null))
        stylesheets.add("/knotable.css")
    }

    init {
        onKeyPressed = EventHandler {
        }
    }

    override fun createDefaultSkin(): Skin<*> {
        return ks;
    }

    init {
        onScroll = EventHandler {
            ks.vsb.value = (ks.vsb.value - it.deltaY * 3 / boundsInLocal.width).coerceIn(0.0, 1.0)
            if (it.deltaY == 0.0) {
                ks.hsb.value = (ks.hsb.value - it.deltaX * 3 / boundsInLocal.height).coerceIn(0.0, 1.0)
            }
        }
    }
}