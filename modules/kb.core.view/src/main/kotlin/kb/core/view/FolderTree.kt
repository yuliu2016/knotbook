package kb.core.view

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kb.core.view.folder.FolderOrTable
import kb.core.view.folder.FolderOrTableCell

@Suppress("unused")
class FolderTree {
    val tree = TreeView<FolderOrTable>()

    private val entityRoot = FolderOrTable("Application", mutableListOf())
    private val root = TreeItem<FolderOrTable>(null)

    init {
        entityRoot.children!!.addAll(listOf(
                FolderOrTable("In-Memory Data", (0..20).map { FolderOrTable("abc.csv") }.toMutableList())
        ))
        tree.setCellFactory { FolderOrTableCell() }
        tree.root = root
        tree.isShowRoot = false
        tree.minWidth = 200.0
        regenerate()
    }

    private fun regenerate() {
        regenerateChildren(root, entityRoot)
    }

    private fun regenerateChildren(root: TreeItem<FolderOrTable>, entityRoot: FolderOrTable) {
        if (entityRoot.children != null && entityRoot.children.isNotEmpty()) {
            root.children.setAll(entityRoot.children.map { entity ->
                TreeItem(entity).also { item ->
                    if (entity.children != null && entity.children.isNotEmpty()) {
                        regenerateChildren(item, entity)
                    }
                }
            })
            root.isExpanded = true
        }
    }
}