package kb.service.api.application;

import kb.service.api.MetaService;
import kb.service.api.ServiceContext;
import org.jetbrains.annotations.NotNull;

public interface ApplicationService extends MetaService {
    void launch(@NotNull ServiceManager manager, @NotNull ServiceContext context);

    default void launchFast() {
        System.out.println("Fast Launching " + this);
    }
}
