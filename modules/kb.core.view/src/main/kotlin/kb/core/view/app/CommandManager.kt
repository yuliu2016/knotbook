package kb.core.view.app

import kb.core.icon.fontIcon
import kb.service.api.ui.Command
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem

class CommandManager {

    val bar = OptionBar().apply {
        hint = "Enter a command"
        textProperty().addListener { _, _, nv ->
        }
        setOnEnterPressed {
            isShowing = false
            invokeCommand(keys[selectedItem])
        }
        setOnHideAndContinue {
            invokeCommand(keys[selectedItem])
        }
    }

    private val commands: MutableMap<String, Command> = LinkedHashMap()
    private val keys: MutableList<String> = ArrayList()

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