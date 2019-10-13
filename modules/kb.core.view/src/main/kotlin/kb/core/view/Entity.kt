package kb.core.view

import javafx.scene.paint.Color
import kb.core.icon.FontIcon
import kb.core.icon.fontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign

class Entity(
        val cat: String? = null,
        val name: String? = null,
        val icon: FontIcon? = null,
        val children: MutableList<Entity>? = null
)