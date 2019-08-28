package kb.core.registry;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import kb.core.code.CodeEditor;
import kb.core.code.Syntax;

import java.util.Arrays;

public class RegistryEditor {
    @SuppressWarnings("unused")
    public static void show() {
        Stage stage = new Stage();
        stage.setTitle("App Registry");

        VBox container = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        container.setPrefWidth(bounds.getWidth() * 0.7);
        container.setPrefHeight(bounds.getHeight() * 0.9);

        String text = Registry.INSTANCE.join();
        TextArea area = new TextArea(text);
        area.setStyle("-fx-background-insets: 0; -fx-border-insets:0; -fx-focus-color: transparent; " +
                "-fx-faint-focus-color:transparent; -fx-font-family:'Roboto Mono', 'Courier New', monospace; " +
                "-fx-font-size:18; -fx-font-smoothing-type: gray");
        VBox.setVgrow(area, Priority.ALWAYS);

        Button discard = new Button("Discard");
        discard.setOnAction(event -> stage.close());

        Button save = new Button("Save");
        save.setOnAction(event -> {
            Registry.INSTANCE.parse(Arrays.asList(area.getText().split("\n")));
            Registry.INSTANCE.save();
            stage.close();
        });

        container.getChildren().add(area);

        HBox buttons = new HBox();

        buttons.setSpacing(16.0);
        buttons.setPadding(new Insets(8.0));
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getChildren().addAll(discard, save);

        container.getChildren().add(buttons);

        Scene scene = new Scene(container);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), stage::close);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public static void ish() {
        new CodeEditor("Application Properties", true,
                "Save", "Discard", Registry.INSTANCE.join(), s -> {
            Registry.INSTANCE.parse(Arrays.asList(s.split("\n")));
            Registry.INSTANCE.save();
        }, Syntax.Properties);
    }
}
