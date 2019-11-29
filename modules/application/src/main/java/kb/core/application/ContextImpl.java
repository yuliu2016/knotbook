package kb.core.application;

import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.application.ApplicationService;
import kb.service.api.data.DataSpace;
import kb.service.api.json.JSONObjectWrapper;
import kb.service.api.ui.UIManager;

class ContextImpl implements ServiceContext {

    Service service;
    ApplicationService app;
    JSONObjectWrapper configCache;

    ContextImpl(Service service, ApplicationService app) {
        this.service = service;
        this.app = app;
    }

    @Override
    public Service getService() {
        return service;
    }

    @Override
    public JSONObjectWrapper getConfig() {
        if (configCache == null) {
            configCache = KnotBook.getKnotBook().getConfig().createConfig(service);
        }
        return configCache;
    }

    @Override
    public UIManager getUIManager() {
        return app.getUIManager();
    }

    @Override
    public DataSpace getDataSpace() {
        return app.getDataSpace();
    }
}
