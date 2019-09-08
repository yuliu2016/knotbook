package kb.application;

import javafx.application.Platform;
import kb.core.context.ApplicationContext;
import kb.core.view.AppView;

public class Main {
    public static void main(String[] args) {
        ApplicationContext.launch(() -> Platform.startup(() -> {
            var inst = AppView.INSTANCE;
            inst.show();
        }));
    }
}
