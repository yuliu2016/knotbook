package kb.service.api.application;

import kb.service.api.Service;
import kb.service.api.TextEditor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PrivilegedContext {

    @NotNull
    TextEditor createTextEditor();

    @NotNull
    ApplicationProps getProps();

    @NotNull
    List<Service> getServices();

}
