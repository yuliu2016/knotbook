package kb.core.view.app

import javafx.scene.input.KeyCombination
import kb.core.icon.IkonResolver
import kb.core.icon.fontIcon
import kb.service.api.ui.Command
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

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
        bar.hint = "Search ${values.size} Commands"
        filteredIndices = keys.indices.toList()
    }

    private fun toItem(command: Command, highlight: BooleanArray?): OptionItem = OptionItem(
            command.name,
            command.shortcut?.displayText?.replace("+", " + "),
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
                val ba = ByteArrayOutputStream()
                e.printStackTrace(PrintWriter(ba))
                Singleton.uiManager.showAlert("Error", ba.toString())
                throw e
            }
        }
    }
}