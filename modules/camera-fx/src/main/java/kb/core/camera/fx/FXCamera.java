package kb.core.camera.fx;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"unused", "WeakerAccess"})
public class FXCamera {

    private ReadOnlyObjectWrapper<Image> imageProperty = new ReadOnlyObjectWrapper<>(null);

    public ReadOnlyObjectProperty<Image> getImageProperty() {
        return imageProperty.getReadOnlyProperty();
    }

    public Image getImage() {
        return getImageProperty().get();
    }

    private ReadOnlyStringWrapper resultProperty = new ReadOnlyStringWrapper(null);

    public ReadOnlyStringProperty getResultProperty() {
        return resultProperty.getReadOnlyProperty();
    }

    public String getResult() {
        return getResultProperty().get();
    }

    private BooleanProperty streamingProperty;

    public BooleanProperty getStreamingProperty() {
        if (streamingProperty == null) {
            streamingProperty = new SimpleBooleanProperty(false);
            streamingProperty.addListener((ob, ov, nv) -> updateStreamingState(nv));
        }
        return streamingProperty;
    }

    public boolean getStreaming() {
        return getStreamingProperty().get();
    }

    public void setStreaming(boolean streaming) {
        getStreamingProperty().set(streaming);
    }

    private BooleanProperty decodingProperty;

    public BooleanProperty getDecodingProperty() {
        if (decodingProperty == null) {
            decodingProperty = new SimpleBooleanProperty(false);
            decodingProperty.addListener((ob, ov, nv) -> decoding = nv);
        }
        return decodingProperty;
    }

    public boolean isDecoding() {
        return getDecodingProperty().get();
    }

    public void setDecoding(boolean decoding) {
        getDecodingProperty().set(decoding);
    }

    private BooleanProperty flippedProperty;

    public BooleanProperty getFlippedProperty() {
        if (flippedProperty == null) {
            flippedProperty = new SimpleBooleanProperty();
            flippedProperty.addListener((ob, ov, nv) -> flipped = nv);
        }
        return flippedProperty;
    }

    public boolean isFlipped() {
        return flippedProperty.get();
    }

    public void setFlipped(boolean flipped) {
        flippedProperty.set(flipped);
    }

    private IntegerProperty webcamIDProperty = new SimpleIntegerProperty(0);

    public IntegerProperty getWebcamIDProperty() {
        return webcamIDProperty;
    }

    public int getWebcamID() {
        return getWebcamIDProperty().get();
    }

    public void setWebcamID(int webcamID) {
        getWebcamIDProperty().set(webcamID);
    }

    public List<String> getWebcamNames() {
        List<String> names = new ArrayList<>();
        for (Webcam wc : getWebcams()) {
            names.add(wc.getName());
        }
        return names;
    }

    private Image image = null;
    private String result = null;
    private Thread thread = null;

    private boolean threadRunning = false;
    private boolean decoding = false;
    private boolean flipped = false;
    private int skippedPulseCounter = 0;

    private Webcam webcam = null;

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            Image capture = image;
            String res = result;
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

    private void updateStreamingState(boolean isStreaming) {
        if (isStreaming) {
            webcam = getWebcams().get(getWebcamID());
            if (webcam != null && !webcam.isOpen()) {
                webcam.setCustomViewSizes(WebcamResolution.VGA.getSize());
                webcam.setViewSize(WebcamResolution.VGA.getSize());
                webcam.open();
            }
            thread = new Thread(this::readCameraStream);
            thread.setDaemon(true);
            thread.start();
            timer.start();
        } else {
            if (thread != null) {
                threadRunning = false;
                thread.interrupt();
            }
            if (webcam != null) {
                webcam.close();
                webcam = null;
            }
            timer.stop();
        }
    }

    private String decode(BufferedImage capture) {
        try {
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(capture);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = getReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        }
    }

    private void readCameraStream() {
        final AtomicReference<WritableImage> imgRef = new AtomicReference<>(null);
        threadRunning = true;
        while (threadRunning) {
            Webcam webcam = this.webcam;
            if (webcam != null) {
                BufferedImage capture = webcam.getImage();
                if (capture != null) {
                    imgRef.set(toFXImageFlipped(capture, imgRef.get(), flipped));
                    capture.flush();
                    String decoded = decoding ? decode(capture) : null;
                    image = imgRef.get();
                    result = decoded;
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
        }
        thread = null;
    }

    private static List<Webcam> webcams = null;

    private static List<Webcam> getWebcams() {
        if (webcams == null) {
            webcams = Webcam.getWebcams();
        }
        return webcams;
    }

    private static MultiFormatReader reader = null;

    private static MultiFormatReader getReader() {
        if (reader == null) {
            reader = new MultiFormatReader();
        }
        return reader;
    }

    private static final int bw = 640;
    private static final int bh = 480;

    private static BufferedImage converted = null;
    private static Graphics2D g2d = null;

    private static int[] fb = null;

    private static void initConverter() {
        converted = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB_PRE);
        g2d = converted.createGraphics();
        fb = new int[bw * bh];
    }

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
    public static WritableImage toFXImageFlipped(BufferedImage source, WritableImage dest, boolean flipped) {
        switch (source.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
                break;
            default:
                if (converted == null) {
                    initConverter();
                }
                g2d.drawImage(source, 0, 0, null);
                source = converted;
                break;
        }
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

        if (flipped) {
            for (int i = 0; i < bh; i++) {
                for (int j = 0; j < bw; j++) {
                    fb[i * bw + j] = data[i * bw + bw - 1 - j];
                }
            }
            pw.setPixels(0, 0, bw, bh, pf, fb, offset, scan);
        } else {
            pw.setPixels(0, 0, bw, bh, pf, data, offset, scan);
        }

        return dest;
    }
}
