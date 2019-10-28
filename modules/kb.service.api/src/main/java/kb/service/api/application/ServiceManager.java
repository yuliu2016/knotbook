package kb.service.api.application;

import kb.service.api.Service;

import java.util.List;

public interface ServiceManager {

    ApplicationProps getProps();

    List<Service> getServices();

    String getVersion();

}
