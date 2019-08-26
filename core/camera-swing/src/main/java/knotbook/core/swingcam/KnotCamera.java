package knotbook.core.swingcam;

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
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import knotbook.core.splash.GCSplash;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.nio.IntBuffer;
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
    private boolean threadRunning = false;
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
                image = null;
            }
            if (res == null) {
                skippedPulseCounter++;
                if (skippedPulseCounter > 45) {
                    resultProperty.set(null);
                }
            } else {
                skippedPulseCounter = 0;
                resultProperty.set(res);
            }
        }
    };

    private void updateStreamingState(boolean state) {
        if (state) {
            if (!webcam.isOpen()) {
                webcam.setCustomViewSizes(WebcamResolution.VGA.getSize());
                webcam.setViewSize(WebcamResolution.VGA.getSize());
                webcam.open();
            }
            thread = new Thread(this::readStream);
            thread.setDaemon(true);
            thread.start();
            timer.start();
        } else {
            if (thread != null) {
                threadRunning = false;
                thread.interrupt();
            }
            webcam.close();
            timer.stop();
            Platform.runLater(GCSplash::splash);
        }
    }

    private String decode(BufferedImage capture) {
        try {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(capture)));
            Result result = reader.decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        }
    }

    private void readStream() {
        final AtomicReference<WritableImage> imgRef = new AtomicReference<>(null);
        threadRunning = true;
        while (threadRunning) {
            BufferedImage capture = webcam.getImage();
            if (capture != null) {
                imgRef.set(toFXImageFlipped(capture, imgRef.get()));
                capture.flush();
                String decoded = decode(capture);
                synchronized (pulseControl) {
                    image = imgRef.get();
                    result = decoded;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
        thread = null;
    }

    private static final int bw = 640;
    private static final int bh = 480;

    private static final BufferedImage converted = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB_PRE);
    private static final Graphics2D g2d = converted.createGraphics();

    private static final int[] fb = new int[bw * bh];

    /**
     * Snapshots the specified {@link BufferedImage} and stores a copy of
     * its pixels into a JavaFX {@link Image} object, creating a new
     * object if needed.
     * The returned {@code Image} will be a static snapshot of the state
     * of the pixels in the {@code BufferedImage} at the time the method
     * completes.  Further changes to the {@code BufferedImage} will not
     * be reflected in the {@code Image}.
     * <p>
     * The optional JavaFX {@link WritableImage} parameter may be reused
     * to store the copy of the pixels.
     * A new {@code Image} will be created if the supplied object is null,
     * is too small or of a type which the image pixels cannot be easily
     * converted into.
     *
     * @param source the {@code BufferedImage} object to be converted
     * @param dest   an optional {@code WritableImage} object that can be
     *               used to store the returned pixel data
     * @return an {@code Image} object representing a snapshot of the
     * current pixels in the {@code BufferedImage}.
     * @since JavaFX 2.2
     */
    public static WritableImage toFXImageFlipped(BufferedImage source, WritableImage dest) {
        switch (source.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
                break;
            default:
                g2d.drawImage(source, 0, 0, null);
                source = converted;
                break;
        }
        // assert(bimg.getType == TYPE_INT_ARGB[_PRE]);
        if (dest != null) {
            int iw = (int) dest.getWidth();
            int ih = (int) dest.getHeight();
            if (iw < bw || ih < bh) {
                dest = null;
            } else if (bw < iw || bh < ih) {
                int[] empty = new int[iw];
                PixelWriter pw = dest.getPixelWriter();
                PixelFormat<IntBuffer> pf = PixelFormat.getIntArgbPreInstance();
                if (bw < iw) {
                    pw.setPixels(bw, 0, iw - bw, bh, pf, empty, 0, 0);
                }
                if (bh < ih) {
                    pw.setPixels(0, bh, iw, ih - bh, pf, empty, 0, 0);
                }
            }
        }
        if (dest == null) {
            dest = new WritableImage(bw, bh);
        }
        PixelWriter pw = dest.getPixelWriter();
        DataBufferInt db = (DataBufferInt) source.getRaster().getDataBuffer();
        int[] data = db.getData();
        int offset = source.getRaster().getDataBuffer().getOffset();
        int scan = 0;
        SampleModel sm = source.getRaster().getSampleModel();
        if (sm instanceof SinglePixelPackedSampleModel) {
            scan = ((SinglePixelPackedSampleModel) sm).getScanlineStride();
        }
        PixelFormat<IntBuffer> pf = (source.isAlphaPremultiplied() ?
                PixelFormat.getIntArgbPreInstance() :
                PixelFormat.getIntArgbInstance());

        // Flip the array

        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                fb[i * bw + j] = data[i * bw + bw - 1 - j];
            }
        }

        pw.setPixels(0, 0, bw, bh, pf, fb, offset, scan);
        return dest;
    }
}
