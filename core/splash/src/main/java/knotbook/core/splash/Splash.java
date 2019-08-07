package knotbook.core.splash;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Splash {
    public static void splash() {
        Stage stage = new Stage();

        VBox root = new VBox();
        root.setStyle("-fx-background-color: transparent");
        root.setPrefWidth(500.0);
        root.setPrefHeight(340.0);

        VBox top = new VBox();
        top.setPadding(new Insets(10.0));
        top.setAlignment(Pos.CENTER);
        top.setStyle("-fx-background-color:rgba(96,96,96,0.9)");

        Image iconImage = new Image(Splash.class.getResourceAsStream("/knotbook/core/splash/knot-tb.png"));
        ImageView icon = new ImageView(iconImage);
        icon.setPreserveRatio(true);
        icon.setFitHeight(80.0);

        Label label = new Label("notbook");
        label.setStyle("-fx-font-size: 72;-fx-font-weight:bold;-fx-text-fill: white");

        HBox header = new HBox();
        header.setAlignment(Pos.BASELINE_CENTER);
        header.setPrefHeight(80.0);
        header.getChildren().addAll(icon, label);

        Label label1 = new Label("Version RISE (Build 3.0.01) - Powered by Restructured Tables ");
        label1.setStyle("-fx-text-fill: white");

        top.getChildren().addAll(header, label1);

        VBox bottom = new VBox();
        bottom.setStyle("-fx-background-color:rgba(0,0,0,0.9)");
        VBox.setVgrow(bottom, Priority.ALWAYS);
        bottom.setAlignment(Pos.TOP_CENTER);
        bottom.setPadding(new Insets(8.0));

        root.getChildren().addAll(top, bottom);

        Scene scene = new Scene(root);
        scene.setRoot(root);
        scene.setFill(Color.TRANSPARENT);

        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) stage.close();
        });

        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(iconImage);
        stage.setAlwaysOnTop(true);
        stage.setTitle("About");
        stage.show();
    }
}
