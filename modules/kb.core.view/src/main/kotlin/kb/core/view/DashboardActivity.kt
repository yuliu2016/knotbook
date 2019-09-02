package kb.core.view

import javafx.scene.control.TreeItem
import kb.core.fx.fontIcon
import kb.core.fx.runOnFxThread
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
        runOnFxThread {
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

    private val entityRoot = Entity(EntityText("Application"), FontIcon.of(ICE_CREAM), null, mutableListOf())
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
        entityRoot.children?.addAll(listOf(
                Entity(EntityText("Memory Repo"), fontIcon(BOLT, 13), null, mutableListOf(
                        Entity("Empty View", fontIcon(MINUS, 13)),
                        Entity(EntityText("2018iri.csv"), fontIcon(TABLE, 13), null, mutableListOf(
                                Entity("Filesystem Link", fontIcon(LINK, 13)),
                                Entity("User Edit Mask", fontIcon(USER_EDIT, 13)),
                                Entity("Formulas", fontIcon(SUPERSCRIPT, 13)),
                                Entity(EntityText("Filter"), fontIcon(FILTER, 13), "Team={865}"),
                                Entity(EntityText("Sort"), fontIcon(SORT_ALPHA_UP, 13), "Scale"),
                                Entity(EntityText("Sort"), fontIcon(SORT_ALPHA_UP, 13), "Switch"),
                                Entity(EntityText("Colour Scale"), fontIcon(PAINT_BRUSH, 13), "Auto Switch")
                        ))
                )),
                Entity(EntityText("Local File", bold = true), fontIcon(DESKTOP, 13), "~/KB/repo"),
                Entity("Android Scouting App", fontIcon(QRCODE, 13)),
                Entity("The Blue Alliance")
        ))
        view.indexTree.setCellFactory { EntityCell() }
        view.indexTree.root = root
        view.indexTree.isShowRoot = false
        regenerate()
    }

    private fun regenerate() {
        regenerateChildren(root, entityRoot)
    }

    private fun regenerateChildren(root: TreeItem<Entity>, entityRoot: Entity) {
        if (entityRoot.children != null && entityRoot.children.isNotEmpty()) {
            root.children.setAll(entityRoot.children.map { entity ->
                TreeItem(entity).also { item ->
                    regenerateChildren(item, entity)
                }
            })
        }
    }
}