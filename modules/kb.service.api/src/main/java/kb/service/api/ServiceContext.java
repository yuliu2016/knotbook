package kb.service.api;

import kb.service.api.ui.CommandManager;
import kb.service.api.ui.NotificationManager;
import kb.service.api.ui.TextEditor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface ServiceContext {
    /**
     * Get the service that initiated this ServiceContext
     */
    @NotNull
    Service getService();

    /**
     * Get the properties that belongs to this context
     */
    @NotNull
    ServiceProps getProps();

    /**
     * Create a text editor instance for text/code editing
     */
    @NotNull
    TextEditor createTextEditor();

    /**
     * Retrieve an instance of the command manager for this context,
     * used to add options to the user interface
     */
    @NotNull
    CommandManager getCommandManager();

    /**
     * Retrieve an instance of the notification manager
     */
    @NotNull
    NotificationManager getNotificationManager();
}
