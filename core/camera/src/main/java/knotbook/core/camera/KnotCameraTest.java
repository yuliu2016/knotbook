package knotbook.core.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.buildin.WebcamDefaultDriver;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class KnotCameraTest {

    public static void test() {
        Webcam.setDriver(new WebcamDefaultDriver());
        KnotCamera camera;
        try {
            camera = new KnotCamera();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Camera Test");

        VBox box = new VBox();

        ImageView view = new ImageView();
        view.imageProperty().bind(camera.imageProperty());
        view.setPreserveRatio(true);
        view.setFitHeight(720.0);

        Label label = new Label();
        label.setStyle("-fx-text-fill: white");
        camera.resultProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                box.setBackground(null);
            } else {
                box.setBackground(new Background(new BackgroundFill(Color.rgb(0, 169, 0), null, null)));
            }
        });
        label.textProperty().bind(camera.resultProperty());

        box.getChildren().addAll(view, label);
        box.setPrefHeight(760.0);
        box.setPrefWidth(960.0);
        box.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(box));
        stage.setOnCloseRequest(event -> {
            camera.setStreaming(false);
        });
        stage.show();
        camera.setStreaming(true);
    }
}
