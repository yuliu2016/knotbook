package kb.core.view.splash;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import kotlin.KotlinVersion;


public class AboutSplash {

    private static Label labelOf(String s) {
        Label label = new Label(s);
        label.setStyle("-fx-text-fill: white");
        return label;
    }

    public static void splash(Window owner) {
        Popup popup = new Popup();

        VBox root = new VBox();
        root.setStyle("-fx-background-color: transparent");
        root.setPrefWidth(480.0);
        root.setPrefHeight(320.0);

        HBox top = new HBox();
        top.setPadding(new Insets(16.0, 32.0, 16.0, 32.0));
        top.setAlignment(Pos.BASELINE_LEFT);
        top.setMaxHeight(32.0);
        top.setStyle("-fx-background-color:rgba(96,96,96,0.9)");

        Image iconImage = new Image(AboutSplash.class.getResourceAsStream("/icon.png"));
        ImageView icon = new ImageView(iconImage);
        icon.setPreserveRatio(true);
        icon.setFitHeight(72.0);

        Label label = new Label("notBook");
        label.setStyle("-fx-font-size: 72;-fx-font-weight:normal;-fx-text-fill: white");
        top.getChildren().addAll(icon, label);

        VBox bottom = new VBox();
        bottom.setStyle("-fx-background-color:rgba(0,0,0,0.9)");
        VBox.setVgrow(bottom, Priority.ALWAYS);
        bottom.setPadding(new Insets(8.0, 32.0, 8.0, 32.0));

        bottom.getChildren().addAll(
                labelOf("Version 3.10.0-ea"),
                labelOf("Licensed under MIT and powered by open-source software"),
                labelOf("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch")),
                labelOf("Java Runtime: " + System.getProperty("java.vm.name") +
                        " " + System.getProperty("java.vm.version")),
                labelOf("JavaFX Build: " + System.getProperty("javafx.runtime.version")),
                labelOf("Kotlin Build: " + KotlinVersion.CURRENT),
                labelOf("Max Heap Size: " +
                        Runtime.getRuntime().maxMemory() / 1024 / 1024 + "M")
        );

        root.getChildren().addAll(top, bottom);

        popup.getContent().add(root);

        popup.centerOnScreen();
        popup.setAutoHide(true);
        popup.show(owner);
    }
}
