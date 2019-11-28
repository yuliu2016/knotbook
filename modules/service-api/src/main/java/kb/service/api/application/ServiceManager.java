package kb.service.api.application;

import kb.service.api.Service;

import java.util.List;

@SuppressWarnings("unused")
public interface ServiceManager {

    String getJSONConfig();

    void setJSONConfig(String json);

    List<Service> getServices();

    String getBuildVersion();

    String getImageVersion();

    void exitOK();

    void exitError();

    List<String> getJVMArgs();
}
