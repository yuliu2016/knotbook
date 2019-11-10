package kb.core.view.app

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kb.core.icon.fontIcon
import kb.service.api.ui.Command
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem

class CommandManager {
    val commandsBar = OptionBar().apply {
        hint = "Enter a command"
    }

    val allItems: ObservableList<OptionItem> = FXCollections.observableArrayList()

    val commands: MutableMap<String, Command> = LinkedHashMap()

    fun registerCommand(id: String, command: Command) {

        allItems.add(OptionItem(
                command.name,
                command.shortcut?.toString(),
                command.icon?.let { fontIcon(it, 14) },
                null
        ))
        commands[id] = command
    }


    fun hasCommand(id: String): Boolean {
        return id in commands
    }

    fun invokeCommand(id: String) {
        // TODO log command
        commands[id]?.callback?.run()
    }
}