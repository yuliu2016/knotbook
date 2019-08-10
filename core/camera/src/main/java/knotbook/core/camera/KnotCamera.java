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
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"unused", "WeakerAccess"})
public class KnotCamera {

    private ReadOnlyObjectWrapper<Image> imageProperty = new ReadOnlyObjectWrapper<>(null);

    public ReadOnlyObjectProperty<Image> imageProperty() {
        return imageProperty.getReadOnlyProperty();
    }

    public Image getImage() {
        return imageProperty().get();
    }

    private ReadOnlyStringWrapper resultProperty = new ReadOnlyStringWrapper(null);

    public ReadOnlyStringProperty resultProperty() {
        return resultProperty.getReadOnlyProperty();
    }

    private BooleanProperty streamingProperty;

    public BooleanProperty streamingProperty() {
        if (streamingProperty == null) {
            streamingProperty = new SimpleBooleanProperty(false);
            streamingProperty.addListener((observable, oldValue, newValue) -> updateStreamingState(newValue));
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
    private Thread thread = null;
    private int skippedPulseCounter = 0;

    private final Webcam webcam = Webcam.getDefault();
    private final Object pulseControl = new Object();
    private final MultiFormatReader reader = new MultiFormatReader();

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            Image capture;
            String res;
            synchronized (pulseControl) {
                capture = image;
                res = result;
            }
            if (capture != null) {
                imageProperty.set(capture);
            }
            if (res == null) {
                skippedPulseCounter++;
                if (skippedPulseCounter > 5) {
                    resultProperty.set(null);
                }
            } else {
                resultProperty.set(res);
            }
        }
    };

    private void updateStreamingState(boolean state) {
        if (state) {
            if (!webcam.isOpen()) {
                webcam.setViewSize(WebcamResolution.VGA.getSize());
                webcam.setCustomViewSizes(WebcamResolution.VGA.getSize());
                webcam.open();
            }
            thread = new Thread(this::readStream);
            thread.setDaemon(true);
            thread.start();
            timer.start();
        } else {
            if (thread != null) {
                thread.interrupt();
            }
            webcam.close();
            timer.stop();
        }
    }

    private String decode(BufferedImage capture) {
        try {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(capture)));
            Result result = reader.decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void readStream() {
        final AtomicReference<WritableImage> imgRef = new AtomicReference<>(null);
        while (!Thread.currentThread().isInterrupted()) {
            BufferedImage capture = webcam.getImage();
            if (capture != null) {
                imgRef.set(SwingFXUtils.toFXImage(capture, imgRef.get()));
                capture.flush();
                String decoded = decode(capture);
                synchronized (pulseControl) {
                    image = imgRef.get();
                    result = decoded;
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
