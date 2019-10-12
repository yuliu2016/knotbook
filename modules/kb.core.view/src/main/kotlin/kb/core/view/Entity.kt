package kb.core.view

import javafx.scene.paint.Color
import kb.core.icon.FontIcon
import kb.core.icon.fontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign

class Entity(
        val text: String? = null,
        val supportText: String? = null,
        val icon: FontIcon = fontIcon(MaterialDesign.MDI_CUBE, 13),
        val color: Color? = null,
        val children: MutableList<Entity>? = null
)