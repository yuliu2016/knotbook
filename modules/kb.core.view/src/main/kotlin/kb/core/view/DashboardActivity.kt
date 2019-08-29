package kb.core.view

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.util.Callback
import kb.core.fx.*
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

    private fun selectAndFocus(item: TreeItem<Index>) {
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
    private fun addAndSelect(index: Index) {
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

    private val root = TreeItem<Index>(null)

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
        view.indexTree.cellFactory = Callback { Cell() }
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
//        root.children.setAll(repositories.map { repository ->
//            TreeItem(Index(repository.title, repository.icon, repository, "", false)).apply {
//                val tables = repository.tables
//                children.clear()
//                tables[""]?.run {
//                    children.addAll(map { TreeItem(it) })
//                }
//                children.addAll(repository.tables.mapNotNull { e ->
//                    if (e.key.isEmpty()) null else
//                        TreeItem(Index(e.key, fontIcon(FOLDER, 18), repository, "", false)).apply {
//                            children.addAll(e.value.map { TreeItem(it) })
//                        }
//                })
//                isExpanded = true
//            }
//        })
//        selectAndSet(root.children.first().children.first())
    }

    inner class Cell : TreeCell<Index>() {

        init {
            onDragDetected = EventHandler { event ->
                if (item == null || !treeItem.isLeaf) {
                    return@EventHandler
                }
                val board = startDragAndDrop(TransferMode.MOVE)
                val content = ClipboardContent()
                content.putString(item.hashCode().toString())
                board.setContent(content)
                event.consume()
            }

            onDragOver = EventHandler { event ->
                if (event.gestureSource !== this@Cell && event.dragboard.hasString() && !isEmpty && !item.isLeaf) {
                    event.acceptTransferModes(TransferMode.MOVE)
                }
                event.consume()
            }

            onDragEntered = EventHandler { event ->
                if (event.gestureSource !== this@Cell && event.dragboard.hasString() && !isEmpty && !item.isLeaf) {
                    styleClass("drag-over")
                }
            }

            onDragExited = EventHandler { event ->
                if (event.gestureSource !== this@Cell && event.dragboard.hasString() && !isEmpty && !item.isLeaf) {
                    noStyleClass()
                }
            }

            onDragDropped = EventHandler { event ->
                if (item == null) {
                    return@EventHandler
                }

                val db = event.dragboard
                var success = false

                if (db.hasString()) {
                    success = true
                }

                event.isDropCompleted = success

                event.consume()
            }

            onDragDone = EventHandler { it.consume() }
        }

        override fun updateItem(item: Index?, empty: Boolean) {
            super.updateItem(item, empty)
            super.updateItem(item, empty)

            if (item == null || empty) {
                graphic = null
            } else {
                alignment = Pos.CENTER_LEFT
                graphic = hbox {
                    alignment = Pos.CENTER_LEFT
                    padding = Insets(0.0, 0.0, 0.0, 4.0)
                    modify {
                        +item.icon.centerIn(24)
                        val a = item.title.substringBeforeLast("/", "")
                        if (a.isEmpty()) {
                            +Label(item.title)
                        } else {
                            +Label("$a/")
                            +Label(item.title.substringAfterLast('/', "")).apply {
                                style = "-fx-font-weight:bold"
                                padding = Insets(0.0)
                            }
                        }
                    }

                    onMouseClicked = EventHandler { event ->
                        if (event.clickCount == 2) {
                            println("hi")
                        }
                    }
                }
            }
        }
    }
}