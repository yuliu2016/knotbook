package kb.application;

import javafx.application.Application;
import javafx.stage.Stage;
import kb.core.view.AppView;

public class KnotBook extends Application {
    @Override
    public void start(Stage stage) {
        AppView.INSTANCE.show();
    }
}
