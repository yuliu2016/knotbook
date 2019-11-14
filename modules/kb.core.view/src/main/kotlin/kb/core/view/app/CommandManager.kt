package kb.core.view.app

import kb.core.icon.IkonResolver
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

    private val keys: MutableList<String> = ArrayList()
    private val values: MutableList<Command> = ArrayList()

    fun setAll() {
        bar.items.setAll(values.map { toItem(it, null) })
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

    private fun updateSearch(ov: String?, nv: String) {
        val q = nv.trim()
        if (q == ov) return
        if (q.isEmpty()) {
            setAll()
        } else {
            bar.items.setAll(values
                    .mapIndexed { index, command -> index to OptionItem.parse(command.name, q) }
                    .filter { it.second != null }
                    .sortedWith(Comparator { o1, o2 ->
                        OptionItem.compare(o2.second, o1.second)
                    })
                    .map { toItem(values[it.first], it.second) }
            )
        }
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
        println("Invoking Command #$id")
        values.getOrNull(keys.indexOf(id))?.callback?.run()
    }
}