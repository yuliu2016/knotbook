package kb.service.api.application;

import kb.service.api.Service;

import java.util.List;

@SuppressWarnings("unused")
public interface ServiceManager {

    ApplicationProps getProps();

    List<Service> getServices();

    String getBuildVersion();

    String getImageVersion();

    void exitOK();
}
