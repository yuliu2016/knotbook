package kb.service.api;

import kb.service.api.ui.CommandManager;
import kb.service.api.ui.Notification;
import kb.service.api.ui.TextEditor;

@SuppressWarnings("unused")
public interface ServiceContext {
    /**
     * Get the service that initiated this ServiceContext
     */
    Service getService();

    /**
     * Get the properties that belongs to this context
     */
    ServiceProps getProps();

    /**
     * Create a text editor instance for text/code editing
     */
    TextEditor createTextEditor();

    /**
     * Create a notification handler
     */
    Notification createNotification();

    /**
     * Retrieve an instance of the command manager for this context,
     * used to add options to the user interface
     */
    CommandManager getCommandManager();
}
