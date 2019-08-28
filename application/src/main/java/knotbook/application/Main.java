package knotbook.application;

import javafx.application.Application;
import javafx.stage.Stage;
import knotbook.core.registry.Registry;
import knotbook.core.view.AppView;

public class Main extends Application {
    public static void main(String[] args) {
        Registry.INSTANCE.load();
        launch(Main.class);
    }

    @Override
    public void start(Stage primaryStage) {
        AppView.INSTANCE.show(primaryStage);
    }
}
