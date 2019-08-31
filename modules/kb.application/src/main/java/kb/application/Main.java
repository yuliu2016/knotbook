package kb.application;

import javafx.application.Application;
import kb.core.context.ApplicationContext;
import kb.core.registry.Registry;

public class Main {
    public static void main(String[] args) {
        ApplicationContext.obtainRoot(() -> {
            Registry.INSTANCE.load();
            Application.launch(KnotBook.class);
        });
    }
}
