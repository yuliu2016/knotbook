package knotbook.core.aero;

import javafx.application.Application;
import javafx.stage.Stage;

public class AeroStageTest extends Application {
    public static void main(String[] args) {
        Application.launch(AeroStageTest.class);
    }

    @Override
    public void start(Stage primaryStage) {
        Stage stage = new Stage();


        stage.show();
    }
}
