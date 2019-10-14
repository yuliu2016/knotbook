package kb.core.view

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kb.core.icon.FontIcon
import kb.core.icon.fontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign.MDI_APPLICATION
import org.kordamp.ikonli.materialdesign.MaterialDesign.MDI_MEMORY

@Suppress("unused")
class IndexTree {
    val tree = TreeView<FolderOrTable>()

    private val entityRoot = FolderOrTable("Application", FontIcon.of(MDI_APPLICATION), mutableListOf())
    private val root = TreeItem<FolderOrTable>(null)

    init {
        entityRoot.children!!.addAll(listOf(
                FolderOrTable("In-Memory Data", fontIcon(MDI_MEMORY, 14))
        ))
        tree.setCellFactory { EntityCell() }
        tree.root = root
        tree.isShowRoot = false
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