package kb.core.view

import javafx.scene.paint.Color

data class EntityText(
        val string: String,
        val color: Color? = null,
        val bold: Boolean = false
)