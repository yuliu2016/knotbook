package kb.application;

import javafx.application.Platform;
import kb.core.view.AppView;

public class Main {
    public static void main(String[] args) {
        ApplicationContext.launch(() -> {
            var inst = AppView.INSTANCE;
            inst.show();
        });
    }
}
