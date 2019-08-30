package kb.core.view

import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TreeItem
import javafx.util.Callback
import kb.core.fx.*
import kb.core.icon.FontIcon
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid.*

@Suppress("unused")
class DashboardActivity {
    internal val view = DashboardView()

//    private val memoryRepository = MemoryRepository()
//
//    private val repositories = listOf(
//            memoryRepository,
//            FSRepository()
//    )
//
//    private val propertyGroups = mutableListOf(view.identityPane)
//
//    private fun setModel(model: ViewModel) {
//        if (window.trySetModel(model)) {
//            propertyGroups.clear()
//            propertyGroups.add(view.identityPane)
//            propertyGroups.addAll(model.getPropertyGroups())
//            view.propertiesBox.panes.setAll(propertyGroups.map { it.pane })
//        }
//    }

    private fun selectAndFocus(item: TreeItem<Entity>) {
        view.indexTree.selectionModel.select(item)
        appRunLater {
            view.indexTree.requestFocus()
        }
    }

//    private fun selectAndSet(item: TreeItem<Index>) {
//        selectAndFocus(item)
//        item.value.model.get()?.let { setModel(it) }
//    }

    @Suppress("UNUSED_PARAMETER")
    private fun addAndSelect(index: Entity) {
        // Requires item to be added to repo AFTER, not BEFORE
//        val item = TreeItem(index)
//        val repoIndex = repositories.indexOf(index.repository)
//        if (repoIndex == -1) return
//        val topLevelContextSize = index.repository.tables[""]?.size ?: 0
//        if (index.context.isEmpty()) {
//            root.children[repoIndex].children.add(topLevelContextSize, item)
//        } else {
//            val tableIndex = index.repository.tables.keys.sorted().indexOf(index.context)
//            if (tableIndex == -1) return
//            root.children[repoIndex].children[topLevelContextSize + tableIndex - 1].children.add(item)
//        }
//        selectAndSet(item)
    }

    private val entityRoot = Entity(FontIcon.of(ICE_CREAM), listOf(), mutableListOf())
    private val root = TreeItem<Entity>(null)

//    init {
//        setContentView(view.splitPane)
//        view.openButton.onMouseClicked = EventHandler {
//            val chooser = FileChooser()
//            chooser.title = "Open"
//            chooser.initialDirectory = File(System.getProperty("user.home") + "/Desktop")
//            chooser.extensionFilters.addAll(
//                    FileChooser.ExtensionFilter("CSV", "*.csv")
//            )
//            val res: File? = chooser.showOpenDialog(view.openButton.scene.window)
//            if (res != null && res.extension.toLowerCase() == "csv") {
//                try {
//                    val data = DataFrame.readDelim(res.inputStream().reader().buffered(),
//                            CSVFormat.DEFAULT.withHeader().withNullString(""))
//                    val model = TableViewModel(data)
//                    val item = Index(res.name, fontIcon(TABLE, 18), memoryRepository, "", true, model)
//                    addAndSelect(item)
//                    memoryRepository.add(item)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }

    init {
        view.indexTree.cellFactory = Callback { EntityCell() }
        view.indexTree.root = root
        view.indexTree.isShowRoot = false
        view.indexTree.contextMenu = ContextMenu().modify {
            submenu {
                name("New")
                modify {
                    item {
                        name("TBA Integration")
                    }
                    item {
                        name("Python Integration")
                    }
                    item {
                        name("Duplicate Table")
                    }
                    item {
                        name("Linked View")
                    }
                }
            }
            +SeparatorMenuItem()
            item {
                name("New Folder")
                icon(FOLDER_OPEN, 18)
            }
            item {
                name("Rename")
                icon(FONT, 18)
            }
            item {
                name("Reload")
                icon(SYNC, 18)
            }
            item {
                name("Delete")
                icon(TRASH_ALT, 18)
            }
            item {
                name("Reveal in Source")
                icon(EYE, 18)
            }
        }
        regenerate()
    }

    private fun regenerate() {
        root.children.setAll()
//        selectAndSet(root.children.first().children.first())
    }


}