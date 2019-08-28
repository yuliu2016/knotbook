package test.knotbook.core.aero;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import knotbook.core.icon.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

public class IconSizeTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        Stage stage = new Stage();

        VBox vBox = new VBox();
        vBox.setPrefWidth(500);
        vBox.setPrefHeight(700);
        vBox.setStyle("-fx-background-color: rgba(255,255,255)");

        for (int i = 8; i < 25; i++) {
            HBox hBox = new HBox();
            hBox.setPadding(new Insets(2, 10, 2, 10));
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().addAll(new Label(String.valueOf(i)),
                    FontIcon.of(MaterialDesign.MDI_MAGNET, i),
                    FontIcon.of(MaterialDesign.MDI_ACCOUNT, i),
                    FontIcon.of(MaterialDesign.MDI_AIRBALLOON, i),
                    FontIcon.of(MaterialDesign.MDI_WINDOW_RESTORE, i)
            );
            vBox.getChildren().add(hBox);
        }

        stage.setScene(new Scene(vBox));
        stage.show();
    }
}
