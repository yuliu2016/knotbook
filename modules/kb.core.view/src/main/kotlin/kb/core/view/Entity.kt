package kb.core.view

import kb.core.fx.fontIcon
import kb.core.icon.FontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign

class Entity(
        val text: EntityText,
        val icon: FontIcon = fontIcon(MaterialDesign.MDI_CUBE, 13),
        val supportText: String? = null,
        val children: MutableList<Entity>? = null
) {
    constructor(text: String) : this(EntityText(text))

    constructor(text: String, icon: FontIcon) : this(EntityText(text), icon)
}