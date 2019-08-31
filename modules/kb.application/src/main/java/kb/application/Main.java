package kb.application;

import javafx.application.Application;
import kb.core.context.ApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext.launch(() -> {
            Application.launch(KnotBook.class);
        });
    }
}
