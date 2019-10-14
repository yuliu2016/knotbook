package kb.core.view

import kb.core.icon.FontIcon

class FolderOrTable(
        val name: String,
        val icon: FontIcon,
        val children: MutableList<FolderOrTable>? = null
)