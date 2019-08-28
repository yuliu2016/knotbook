package knotbook.core.aero;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import knotbook.core.aero.borderless.BorderlessScene;

public class AeroStageTest extends Application {
    public static void main(String[] args) {
        Application.launch(AeroStageTest.class);
    }

    @Override
    public void start(Stage primaryStage) {
        Stage stage = new Stage();

        VBox vBox = new VBox();
        vBox.setPrefWidth(500);
        vBox.setPrefHeight(700);
        vBox.setStyle("-fx-background-color: rgba(255,255,255,0.9)");

        HBox titleBar = new HBox();
        titleBar.setPrefHeight(30.0);
        titleBar.setMinHeight(30.0);
        titleBar.setStyle("-fx-background-color: #f0f0f0");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(0.0, 0.0, 0.0, 12.0));
        titleBar.setSpacing(12.0);

        HBox imageContainer = new HBox();
        imageContainer.setPrefWidth(45.0);

        Image icon = new Image("file:\\\\\\C:\\Users\\Yu\\IdeaProjects\\knotbook\\core\\aero\\src\\main\\resources\\icon.png");

        ImageView imageView = new ImageView(icon);
        imageView.setFitHeight(20.0);
        imageView.setPreserveRatio(true);
        imageContainer.getChildren().add(imageView);

        titleBar.getChildren().addAll(imageView, new Label("hi"));
        vBox.getChildren().add(titleBar);

        BorderlessScene scene = new BorderlessScene(stage, vBox);
        scene.setMoveControl(titleBar);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.getIcons().add(icon);
        stage.show();
    }
}
