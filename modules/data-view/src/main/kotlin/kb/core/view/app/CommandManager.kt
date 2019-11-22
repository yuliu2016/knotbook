package kb.core.view.app

import javafx.scene.input.KeyCombination
import kb.core.icon.IkonResolver
import kb.core.icon.fontIcon
import kb.service.api.ui.Command
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem

class CommandManager {

    val bar = OptionBar().apply {
        textProperty().addListener { _, ov, nv ->
            val q = nv.trim()
            if (q != ov?.trim()) {
                if (q.isEmpty()) {
                    if (isShowing) {
                        setAll()
                    }
                } else updateSearch(q)
            }
        }
        setOnEnterPressed {
            isShowing = false
            val i = filteredIndices[selectedItem]
            invokeCommand(keys[i], values[i].callback)
        }
    }

    private val keys: MutableList<String> = ArrayList()
    private val values: MutableList<Command> = ArrayList()
    private val referenceItems: MutableList<OptionItem> = ArrayList()

    private var filteredIndices: List<Int> = ArrayList()

    fun forEachShortcut(func: (shortcut: KeyCombination, key: String) -> Unit) {
        for (i in values.indices) {
            val v = values[i]
            if (v.shortcut != null) {
                func(v.shortcut, keys[i])
            }
        }
    }

    fun setAll() {
        for (item in referenceItems) {
            item.highlight = null
        }
        bar.items.setAll(referenceItems)
        bar.hint = "Search ${values.size} Commands"
        filteredIndices = keys.indices.toList()
    }


    private fun updateSearch(q: String) {
        for (i in keys.indices) {
            referenceItems[i].highlight = OptionItem.parse(values[i].name, q)
        }
        filteredIndices = keys.indices
                .filter { i -> referenceItems[i].highlight != null }
                .sortedWith(Comparator { i, j ->
                    OptionItem.compare(referenceItems[j].highlight, referenceItems[i].highlight)
                })
        bar.items.setAll(filteredIndices.map { i -> referenceItems[i] })
    }

    fun registerCommand(id: String, command: Command) {
        if (id !in keys) {
            keys.add(id)
            values.add(command)
            referenceItems.add(OptionItem(
                    command.name,
                    command.shortcut?.displayText?.replace("+", " + "),
                    null,
                    command.icon?.let { code ->
                        IkonResolver.resolveIcon(code)?.let { icon ->
                            fontIcon(icon, 14)
                        }
                    }, null
            ))
        } else throw IllegalStateException("The Command $id is already registered")
    }

    fun hasCommand(id: String): Boolean {
        return id in keys
    }

    fun invokeCommand(id: String) {
        val command = values.getOrNull(keys.indexOf(id))
        if (command == null) {
            println("Command #$id Not Found ")
        } else {
            invokeCommand(id, command.callback)
        }
    }

    private fun invokeCommand(key: String, callback: Runnable?) {
        if (callback == null) {
            println("Command #$key Cannot be Invoked")
        } else {
            println("Invoking Command #$key")
            try {
                callback.run()
            } catch (e: Exception) {
                Singleton.uiManager.showException(e)
                throw e
            }
        }
    }
}