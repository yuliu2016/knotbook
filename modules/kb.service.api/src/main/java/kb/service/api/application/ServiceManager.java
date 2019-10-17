package kb.service.api.application;

import kb.service.api.Service;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ServiceManager {

    @NotNull
    ApplicationProps getProps();

    @NotNull
    List<Service> getServices();

    @NotNull
    String getVersion();

}
