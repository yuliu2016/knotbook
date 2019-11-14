package kb.core.view.app

import javafx.scene.input.KeyCombination
import kb.core.icon.IkonResolver
import kb.core.icon.fontIcon
import kb.service.api.ui.Command
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem

class CommandManager {

    val bar = OptionBar().apply {
        hint = "Search Commands"
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
            values[filteredIndices[selectedItem]].callback?.run()
        }
    }

    private val keys: MutableList<String> = ArrayList()
    private val values: MutableList<Command> = ArrayList()

    private var filteredIndices: List<Int> = ArrayList()

    fun forEachShortcut(func: (shortcut: KeyCombination, key: String) -> Unit) {
        for (i in values.indices) {
            val v = values[i]
            if (v.shortcut != null && v.callback != null) {
                func(v.shortcut, keys[i])
            }
        }
    }

    fun setAll() {
        bar.items.setAll(values.map { toItem(it, null) })
        filteredIndices = keys.indices.toList()
    }

    private fun toItem(command: Command, highlight: BooleanArray?): OptionItem = OptionItem(
            command.name,
            command.shortcut?.displayText,
            command.icon?.let { code ->
                IkonResolver.resolveIcon(code)?.let { icon ->
                    fontIcon(icon, 14)
                }
            },
            highlight
    )

    private fun updateSearch(q: String) {
        val items = values
                .mapIndexed { index, command -> index to OptionItem.parse(command.name, q) }
                .filter { it.second != null }
                .sortedWith(Comparator { o1, o2 ->
                    OptionItem.compare(o2.second, o1.second)
                })
        filteredIndices = items.map { it.first }
        bar.items.setAll(items.map { toItem(values[it.first], it.second) })
    }

    fun registerCommand(id: String, command: Command) {
        if (id !in keys) {
            keys.add(id)
            values.add(command)
        }
    }

    fun hasCommand(id: String): Boolean {
        return id in keys
    }

    fun invokeCommand(id: String) {
        val callback = values.getOrNull(keys.indexOf(id))?.callback
        if (callback == null) {
            println("Command #$id Not Found or Cannot be Invoked")
        } else {
            callback.run()
            println("Invoking Command #$id")
        }
    }
}