package kb.service.api.application;

import kb.service.api.MetaService;
import kb.service.api.ServiceContext;
import kb.service.api.ui.CommandManager;
import kb.service.api.ui.Notification;

public interface ApplicationService extends MetaService {
    void launch(ServiceManager manager, ServiceContext context);

    /**
     * Retrieve an instance of the command manager for this context,
     * used to add options to the user interface
     */

    CommandManager getCommandManager();


    Notification createNotification();
}
