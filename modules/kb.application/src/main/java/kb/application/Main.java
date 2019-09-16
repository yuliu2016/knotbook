package kb.application;

import kb.core.view.AppView;

public class Main {
    public static void main(String[] args) {
        JVMInstance.args = args;
        Application.launch(() -> {
            var inst = AppView.INSTANCE;
            inst.show();
        });
    }
}
