package kb.core.view.folder

class FolderOrTable(
        val name: String,
        val children: MutableList<FolderOrTable>? = null
)