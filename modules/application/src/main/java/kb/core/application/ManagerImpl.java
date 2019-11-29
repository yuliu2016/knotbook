package kb.core.application;

import kb.service.api.Service;
import kb.service.api.application.ServiceManager;

import java.util.List;

class ManagerImpl implements ServiceManager {

    private KnotBook kb;

    public ManagerImpl(KnotBook kb) {
        this.kb = kb;
    }

    @Override
    public String getJSONConfig() {
        return kb.getConfig().getJoinedText();
    }

    @Override
    public void setJSONConfig(String json) {
        kb.getConfig().setInputText(json);
    }

    @Override
    public List<Service> getServices() {
        return kb.getServices();
    }

    @Override
    public String getBuildVersion() {
        return kb.getBuildVersion();
    }

    @Override
    public String getImageVersion() {
        return kb.getImageVersion();
    }

    @Override
    public void exitOK() {
        kb.exit();
    }

    @Override
    public void exitError() {
        System.exit(1);
    }

    @Override
    public List<String> getJVMArgs() {
        return kb.getJVMArgs();
    }
}
