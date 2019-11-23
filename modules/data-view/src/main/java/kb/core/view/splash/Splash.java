package kb.core.view.splash;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import kb.core.view.app.Singleton;
import kb.service.api.ui.UIHelper;
import kotlin.KotlinVersion;


public class Splash {

    private static Label labelOf(String s) {
        Label label = new Label(s);
        label.setStyle("-fx-text-fill: white");
        return label;
    }

    public static void info(Window owner) {
        Popup popup = new Popup();

        VBox root = new VBox();
        root.setStyle("-fx-background-color: transparent");
        root.setPrefWidth(480.0);
        root.setPrefHeight(320.0);

        HBox top = new HBox();
        top.setPadding(new Insets(16.0, 32.0, 0.0, 32.0));
        top.setAlignment(Pos.BASELINE_LEFT);
        top.setPrefHeight(72.0);
        top.setStyle("-fx-background-color:rgba(96,96,96,0.9)");

        Image iconImage = new Image(Splash.class.getResourceAsStream("/icon.png"));
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
                labelOf("KnotBook DataView " + Singleton.INSTANCE.getManager().getVersion()),
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

    public static void gc() {
        Thread thread = new Thread(() -> {
            final Runtime runtime = Runtime.getRuntime();
            final long before = (runtime.totalMemory() - runtime.freeMemory());
            runtime.gc();
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
            Platform.runLater(() -> {
                long now = (runtime.totalMemory() - runtime.freeMemory());
                String mem = String.format("Currently Used Memory: %.3f MB", now / 1024.0 / 1024.0);
                String freed = String.format("Freed Memory: %.3f MB", (before - now) / 1024.0 / 1024.0);
                String msg = mem + "\n" + freed;
                Singleton.INSTANCE
                        .getContext()
                        .getUIManager()
                        .showAlert("JVM", msg);
            });
        });
        thread.start();
    }

    public static void error(Thread t, Throwable e) {
        if (e == null) return;
        String trace = UIHelper.getStackTrace(e);
        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK);

        Label errorLabel = new Label(trace);
        errorLabel.setStyle("-fx-font-size: 10; -fx-text-fill: darkred");

        pane.setContent(errorLabel);

        dialog.setTitle("Exception in thread \"" + t.getName() + "\"");

        ClipboardContent content = new ClipboardContent();
        content.putString(trace);

        Clipboard.getSystemClipboard().setContent(content);
        dialog.showAndWait();
    }
}
