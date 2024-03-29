package kb.service.api.application;

import kb.service.api.MetaService;
import kb.service.api.ServiceContext;
import kb.service.api.data.DataSpace;
import kb.service.api.ui.UIManager;

public interface ApplicationService extends MetaService {

    /**
     * Launch the application service
     */
    void launch(ServiceManager manager, ServiceContext context, Runnable serviceLauncher);

    /**
     * Retrieve an instance of the UI manager for this context,
     * used to add options to the user interface
     */
    UIManager getUIManager();

    /**
     * Retrieve an instance of the data space, which can manipulate
     * all the data
     */
    DataSpace getDataSpace();
}
