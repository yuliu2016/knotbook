package knotbook.core.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ALL")
public class KnotCamera {

    private ReadOnlyObjectWrapper<Image> imageProperty = new ReadOnlyObjectWrapper<>(null);

    public ReadOnlyObjectProperty<Image> imageProperty() {
        return imageProperty.getReadOnlyProperty();
    }

    public Image getImage() {
        return imageProperty.get();
    }

    private ReadOnlyStringWrapper resultProperty = new ReadOnlyStringWrapper(null);

    public ReadOnlyStringProperty resultProperty() {
        return resultProperty.getReadOnlyProperty();
    }

    private BooleanProperty streamingProperty;

    public BooleanProperty streamingProperty() {
        if (streamingProperty == null) {
            streamingProperty = new SimpleBooleanProperty(false);
            streamingProperty.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    if (!webcam.isOpen()) {
                        webcam.setViewSize(WebcamResolution.VGA.getSize());
                        webcam.setCustomViewSizes(WebcamResolution.VGA.getSize());
                        webcam.open();
                    }
                    Thread thread = new Thread(() -> {
                        final AtomicReference<WritableImage> imgRef = new AtomicReference<WritableImage>();
                        int notFoundCount = 0;
                        String lastEntry = "";
                        while (!Thread.currentThread().isInterrupted()) {
                            BufferedImage image = webcam.getImage();
                            webcam.getImageBytes();
                            if (image != null) {
                                imgRef.set(SwingFXUtils.toFXImage(image, imgRef.get()));
                                image.flush();
                                Platform.runLater(() -> {
                                    imageProperty.set(imgRef.get());
                                });
                                try {
                                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
                                    Result result = reader.decode(bitmap);
                                    String text = result.getText();
                                } catch (NotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    webcam.close();
                    timer.stop();
                }
            });
        }
        return streamingProperty;
    }

    public boolean getStreaming() {
        return streamingProperty().get();
    }

    public void setStreaming(boolean streaming) {
        streamingProperty().set(streaming);
    }

    private Image image = null;
    private String result = null;

    private Webcam webcam = Webcam.getDefault();

    private Object pulseControl = new Object();

    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            synchronized (pulseControl) {
                Image image = KnotCamera.this.image;
                if (image != null) {
                    imageProperty.set(image);
                }
                String result = KnotCamera.this.result;
                if (result != null) {
                    resultProperty.set(result);
                }
            }
        }
    };

    MultiFormatReader reader = new MultiFormatReader();

    public KnotCamera() {

    }
}
