package kb.core.view.app

import kb.core.icon.fontIcon
import kb.service.api.ui.Command
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem

class CommandManager {

    val bar = OptionBar().apply {
        hint = "Search Commands"
        textProperty().addListener { _, ov, nv ->
            updateSearch(ov, nv)
        }
        setOnEnterPressed {
            isShowing = false
            invokeCommand(keys[selectedItem])
        }
    }

    private val commands: MutableMap<String, Command> = LinkedHashMap()
    private val keys: MutableList<String> = ArrayList()

    fun setAll() {
        bar.items.setAll(commands.values.map {
            OptionItem(
                    it.name,
                    it.shortcut?.toString(),
                    it.icon?.let { ik -> fontIcon(ik, 14) },
                    null
            )
        })
    }

    private fun updateSearch(ov: String?, nv: String) {
        val q = nv.trim()
        if (q == ov) return
        if (q.isEmpty()) {
            setAll()
        } else {
            val search = commands.values
                    .mapIndexed { index, command -> index to OptionItem.parse(command.name, q) }
                    .filter { it.second != null }
                    .sortedWith(Comparator { o1, o2 ->
                        OptionItem.compare(o2.second, o1.second)
                    })
            val opList = mutableListOf<OptionItem>()
            val v = commands.values.toList()
            for (i in search) {
                val command = v[i.first]
                opList.add(OptionItem(
                        command.name,
                        command.shortcut?.toString(),
                        command.icon?.let { ik -> fontIcon(ik, 14) },
                        i.second
                ))
            }
            bar.items.setAll(opList)
        }
    }

    fun registerCommand(id: String, command: Command) {
        bar.items.add(OptionItem(
                command.name,
                command.shortcut?.toString(),
                command.icon?.let { fontIcon(it, 14) },
                null
        ))
        if (id !in commands) {
            commands[id] = command
            keys.add(id)
        }
    }

    fun hasCommand(id: String): Boolean {
        return id in commands
    }

    fun invokeCommand(id: String) {
        println("Invoking Command #$id")
        commands[id]?.callback?.run()
    }
}