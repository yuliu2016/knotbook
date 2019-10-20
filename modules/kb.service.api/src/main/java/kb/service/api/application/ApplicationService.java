package kb.service.api.application;

import kb.service.api.MetaService;
import kb.service.api.ServiceContext;
import kb.service.api.ui.CommandManager;
import kb.service.api.ui.Notification;
import org.jetbrains.annotations.NotNull;

public interface ApplicationService extends MetaService {
    void launch(@NotNull ServiceManager manager, @NotNull ServiceContext context);

    /**
     * Retrieve an instance of the command manager for this context,
     * used to add options to the user interface
     */
    @NotNull
    CommandManager getCommandManager();

    @NotNull
    Notification createNotification();
}
