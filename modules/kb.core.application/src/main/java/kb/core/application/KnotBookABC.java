package kb.core.application;

import kb.service.abc.ABC;
import kb.service.api.application.JVMInstance;

public class KnotBookABC implements ABC {
    @Override
    public void launch(String[] args) {
        JVMInstance.setArgs(args);
        KnotBook.getKnotBook().launch();
    }
}
