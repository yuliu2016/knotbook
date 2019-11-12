package kb.service.api;

import kb.service.api.json.JSONObjectWrapper;
import kb.service.api.ui.TextEditor;
import kb.service.api.ui.UIManager;

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
     * Get configuration
     */
    JSONObjectWrapper getConfig();

    /**
     * Create a text editor instance for text/code editing
     */
    TextEditor createTextEditor();

    /**
     * Retrieve an instance of the UI manager for this context,
     * used to add options to the user interface
     */
    UIManager getUIManager();
}
