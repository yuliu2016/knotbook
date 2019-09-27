package kb.application;

import kb.core.view.AppView;
import kb.service.api.application.JVMInstance;

public class Main {
    public static void main(String[] args) {
        JVMInstance.setArgs(args);
        Application.launch(() -> {
            var inst = AppView.INSTANCE;
            inst.show();
        });
    }
}
