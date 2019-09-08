package kb.core.splash;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.net.URI;

public class AboutSplash {

    private static Label labelOf(String s) {
        Label label = new Label(s);
        label.setStyle("-fx-text-fill: white");
        return label;
    }

    public static void splash() {
        Stage stage = new Stage();

        VBox root = new VBox();
        root.setStyle("-fx-background-color: transparent");
        root.setPrefWidth(500.0);
        root.setPrefHeight(340.0);

        HBox top = new HBox();
        top.setPadding(new Insets(10.0));
        top.setAlignment(Pos.BASELINE_CENTER);
        top.setPrefHeight(80.0);
        top.setStyle("-fx-background-color:rgba(96,96,96,0.9)");

        Image iconImage = new Image(AboutSplash.class.getResourceAsStream("/kb/core/splash/icon.png"));
        ImageView icon = new ImageView(iconImage);
        icon.setPreserveRatio(true);
        icon.setFitHeight(80.0);

        Label label = new Label("notBook");
        label.setStyle("-fx-font-size: 72;-fx-font-weight:bold;-fx-text-fill: white");
        top.getChildren().addAll(icon, label);

        VBox bottom = new VBox();
        bottom.setStyle("-fx-background-color:rgba(0,0,0,0.9)");
        VBox.setVgrow(bottom, Priority.ALWAYS);
        bottom.setAlignment(Pos.TOP_CENTER);
        bottom.setPadding(new Insets(8.0));
        bottom.setSpacing(8.0);

        Hyperlink hyperlink = new Hyperlink("github.com/yuliu2016/knotbook");
        hyperlink.setStyle("-fx-border-width: 0");
        hyperlink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/yuliu2016/knotbook"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        bottom.getChildren().addAll(
                labelOf("Version LAUNCH (Build 3.0.0-ea)"),
                hyperlink,
                labelOf("Licensed under MIT and powered by open-source software"),
                labelOf("Runtime: " + System.getProperty("java.vm.name") +
                        " " + System.getProperty("java.vm.version") +
                        " on " + System.getProperty("os.name")),
                labelOf("Max Processors: " + Runtime.getRuntime().availableProcessors() + "; Max Heap: " +
                        Runtime.getRuntime().maxMemory() / 1024 / 1024 + "M")
        );

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
