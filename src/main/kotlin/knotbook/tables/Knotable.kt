package knotbook.tables

import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.control.Control
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color

class Knotable : Control() {

    private val ks = KnotableSkin(this)

    init {
        skin = ks
        background = Background(BackgroundFill(Color.WHITE, null, null))
        stylesheets.add("/knotable.css")
    }

    init {
        onKeyPressed = EventHandler {
            ks.cells[1].text = it.text
        }
    }

    init {
        onScroll = EventHandler {
            ks.hsb.value = (ks.hsb.value - it.deltaX * 3 / boundsInLocal.height).coerceIn(0.0, 1.0)
            ks.vsb.value = (ks.vsb.value - it.deltaY * 3 / boundsInLocal.width).coerceIn(0.0, 1.0)
        }
    }
}