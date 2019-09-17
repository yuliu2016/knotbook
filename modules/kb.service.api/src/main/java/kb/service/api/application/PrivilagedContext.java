package kb.service.api.application;

import kb.service.api.ServiceContext;
import kb.service.api.TextEditor;

public interface PrivilagedContext {
    ApplicationService getService();

    TextEditor createTextEditor();

    ServiceContext[] getContexts();

}
