package knotbook.core.table

import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color

class Knotable : Control() {

    init {
        background = Background(BackgroundFill(Color.WHITE, null, null))
        stylesheets.add("/knotable.css")
    }

    override fun createDefaultSkin(): Skin<*> {
        return KnotableSkin(this);
    }
}