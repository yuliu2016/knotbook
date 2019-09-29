package kb.service.api.application;

import kb.service.api.MetaService;
import org.jetbrains.annotations.NotNull;

public interface ApplicationService extends MetaService {
    void launch(@NotNull PrivilagedContext context);

    default void launchFast() {
        System.out.println("Fast Launching " + this);
    }
}
