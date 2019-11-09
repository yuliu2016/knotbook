package kb.core.view.app

import kb.core.icon.fontIcon
import kb.service.api.ui.*

@Suppress("MemberVisibilityCanBePrivate")
class WindowUIManager : UIManager {

    private val commandsBar = OptionBar().apply {
        hint = "Enter a command"
    }

    val commands: MutableMap<String, Command> = LinkedHashMap()

    private var optionBarShown = false

    override fun isOptionBarShown(): Boolean {
        return optionBarShown
    }

    override fun showOptionBar(optionBar: OptionBar?) {
        Singleton.focusedWindow?.showOptionBarPrototype()
    }

    override fun hideOptionBar() {
    }

    override fun registerCommand(id: String, command: Command) {
        commandsBar.items.add(OptionBarItem(
                command.name,
                command.shortcut?.toString(),
                command.icon?.let { fontIcon(it, 14) }
        ))
        commands[id] = command
    }

    override fun hasCommand(id: String): Boolean {
        return id in commands
    }

    override fun invokeCommand(id: String) {
        // TODO log command
        commands[id]?.callback?.run()
    }

    override fun createNotification(): Notification {
        return EventNotification()
    }
}