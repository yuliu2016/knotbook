package kb.core.view

import kb.core.icon.FontIcon

class Entity(
        val icon: FontIcon,
        val text: List<EntityText>,
        val children: MutableList<Entity>?
)