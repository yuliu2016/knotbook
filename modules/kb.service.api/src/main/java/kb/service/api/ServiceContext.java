package kb.service.api;

import kb.service.api.ui.CommandManager;
import kb.service.api.ui.NamedAction;
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

    void pushInfo(@NotNull String message);

    void pushWarning(@NotNull String message);

    void pushError(@NotNull String message);

    void pushActionablePopup(
            @NotNull String message,
            @NotNull NamedAction... commands
    );
}
