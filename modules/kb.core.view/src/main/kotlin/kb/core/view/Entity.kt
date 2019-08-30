package kb.core.view

import kb.core.fx.fontIcon
import kb.core.icon.FontIcon
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid

class Entity(
        val text: List<EntityText>,
        val icon: FontIcon? = null,
        val children: MutableList<Entity>? = null
) {
    constructor(text: String) : this(listOf(EntityText(text)), fontIcon(FontAwesomeSolid.CUBE, 13))

    constructor(text: String, icon: FontIcon) : this(listOf(EntityText(text)), icon)
}