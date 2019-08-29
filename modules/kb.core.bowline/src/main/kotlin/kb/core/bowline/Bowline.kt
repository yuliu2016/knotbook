package kb.core.bowline

import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color

class Bowline : Control() {

    init {
        background = Background(BackgroundFill(Color.WHITE, null, null))
        styleClass.add("knot-table")
    }

    override fun createDefaultSkin(): Skin<*> {
        return BowlineSkin(this)
    }
}