package kb.service.api;

import kb.service.api.optionbar.OptionBar;
import kb.service.api.textedit.TextEditor;
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
     * Create an option bar for user inputs
     */
    OptionBar createOptionBar();
}
