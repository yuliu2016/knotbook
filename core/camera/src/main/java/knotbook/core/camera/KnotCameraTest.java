package knotbook.core.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.vlcj.VlcjDriver;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.medialist.MediaListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KnotCameraTest {

    private static List<MediaListItem> EMPTY = new ArrayList<MediaListItem>();
    /* NOTE!
     *
     * The vlclib does not implement video device discovery on Windows.
     * Therefore, to make it working on this operating system one needs
     * to manually provide the list of media list items from vlcj. This
     * is not necessary on Linux and Mac.
     */

    private static final MediaListItem dev0 = new MediaListItem("HP HD Webcam [Fixed]", "dshow://", EMPTY);
    private static final MediaListItem dev1 = new MediaListItem("USB2.0 HD UVC WebCam", "dshow://", EMPTY);
    private static final MediaListItem dev2 = new MediaListItem("Logitech Webcam", "dshow://", EMPTY);

    public static void test() {
        Webcam.setDriver(new VlcjDriver(Arrays.asList(dev1, dev0, dev2)));
//        System.out.println(Webcam.getDefault());
        KnotCamera camera = new KnotCamera();

        Stage stage = new Stage();

        VBox box = new VBox();

        ImageView view = new ImageView();
        view.imageProperty().bind(camera.imageProperty());
        view.setPreserveRatio(true);
        view.setFitHeight(480);

        Label label = new Label();
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #0a0");
        label.textProperty().bind(camera.resultProperty());

        box.getChildren().addAll(view, label);
        box.setPrefHeight(500.0);
        box.setPrefWidth(640.0);
        box.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(box));
        stage.setOnCloseRequest(event -> {
            camera.setStreaming(false);
        });
        stage.show();
        camera.setStreaming(true);
    }
}
