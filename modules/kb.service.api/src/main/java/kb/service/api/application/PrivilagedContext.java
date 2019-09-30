package kb.service.api.application;

import kb.service.api.ServiceContext;
import kb.service.api.TextEditor;
import org.jetbrains.annotations.NotNull;

public interface PrivilagedContext {
    @NotNull
    ApplicationService getService();

    @NotNull
    TextEditor createTextEditor();

    @NotNull
    ServiceContext[] getContexts();

    @NotNull
    ApplicationProps getProps();

}
