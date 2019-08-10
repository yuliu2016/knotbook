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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ALL")
public class KnotCamera {

    ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    Webcam webcam = Webcam.getDefault();

    BooleanProperty streamingProperty = new SimpleBooleanProperty();
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {

        }
    };

    MultiFormatReader reader = new MultiFormatReader();

    public KnotCamera() {
    }

    private void startCameraStream() {
        if (webcam.isOpen()) webcam.close();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.setCustomViewSizes(WebcamResolution.VGA.getSize());
        webcam.open();
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
//            while (isStreaming) {
//                image = webcam.image
//                if (image != null) {
//                    try {
//                        val result = MultiFormatReader().decode(BinaryBitmap(
//                                HybridBinarizer(BufferedImageLuminanceSource(image))))
//                        notFoundCount = 0
//                        val resultText = result.text
//                        if (resultText != lastEntry) {
//                            Platform.runLater { onQRCodeResult(resultText) }
//                            lastEntry = resultText
//                        }
//                    } catch (ignored:NotFoundException) {
//                        notFoundCount++
//                        if (notFoundCount > 3) {
//                            Platform.runLater { onNoQRCodeFound() }
//                            lastEntry = ""
//                            notFoundCount = 0
//                        }
//                    }
//
//                    imgRef.set(SwingFXUtils.toFXImage(image, imgRef.get()))
//                    image.flush()
//                    Platform.runLater { imageProperty.set(imgRef.get()) }
//                }
//            }
//            webcam.close()
//        }
//        thread.isDaemon = false
//        thread.start()
//        streamImageView.imageProperty().bind(imageProperty)
        });
    }

    /*lateinit var streamImageView: ImageView
    lateinit var imageContainer: VBox
    lateinit var resultLabel: Label
    lateinit var pauseResume: Button
    lateinit var scanList: ListView<V5Entry>

    private lateinit var resultProperty: StringProperty

    private var isStreaming: Boolean = false
    private val webcam = Webcam.getDefault()
    private val imageProperty = SimpleObjectProperty<Image>()
    private val scannerEntries = FXCollections.observableArrayList<V5Entry>()
    private var previousEntries = mutableListOf<String>()

    private val longFormatter = SimpleDateFormat("yyyy-dd-MM HH.mm.ss")
    private val shortFormatter = SimpleDateFormat("HH.mm.ss")

    fun initialize() {
        resultProperty = resultLabel.textProperty()
        resultLabel.lineSpacing = 5.0
        startCameraStream()
        scanList.items = scannerEntries
        initializeListFactory()
        streamImageView.fitWidthProperty().bind(imageContainer.widthProperty())
        streamImageView.fitHeightProperty().bind(imageContainer.heightProperty())
        onNoQRCodeFound()
    }

    internal fun stopCameraStream() {
        isStreaming = false
    }

    fun onCameraStateChange() {
        if (isStreaming) {
            pauseResume.text = "Resume"
            stopCameraStream()
        } else {
            pauseResume.text = "Pause"
            pauseResume.requestLayout()
            Platform.runLater { startCameraStream() }
        }
    }

    fun onSave() {
        if (scannerEntries.isEmpty()) return
                val match = scannerEntries[0].match
        var uniformMatch = true
        for (i in 1 until scannerEntries.size) {
            if (match != scannerEntries[i].match) {
                uniformMatch = false
            }
        }
        val fileName = if (uniformMatch) "$match ${shortFormatter.format(Date())}" else longFormatter.format(Date())
        val f = File(Humber.root, "$fileName.txt")
        f.writeText(previousEntries.joinToString("\n"))
        Alert(Alert.AlertType.NONE, "File Path: ${f.absolutePath}", ButtonType.OK).apply {
            title = "Saved Data"
            initStyle(StageStyle.UTILITY)
            showAndWait()
        }
        scannerEntries.clear()
        previousEntries.clear()
    }

    fun onUndo() {
        if (scannerEntries.isNotEmpty()) {
            scannerEntries.removeAt(scannerEntries.size - 1)
            previousEntries.removeAt(previousEntries.size - 1)
        }
    }

    private fun initializeListFactory() {
        scanList.isMouseTransparent = true
        scanList.isFocusTraversable = false
        scanList.setCellFactory {
            object : ListCell<V5Entry>() {
                override fun updateItem(item: V5Entry?, empty: Boolean) {
                    super.updateItem(item, empty)
//                    prefHeight = 50.0
                    if (empty || item == null) {
                        text = null
                        graphic = null
                        return
                    }
                    graphic = cellFromEntry(item)
                }
            }
        }
    }

    private fun startCameraStream() {
        if (webcam.isOpen) webcam.close()
        webcam.viewSize = WebcamResolution.VGA.size
        webcam.setCustomViewSizes(WebcamResolution.VGA.size)
        webcam.open()
        isStreaming = true
        val thread = Thread {
            val imgRef = AtomicReference<WritableImage>()
            var image: BufferedImage?
                    var notFoundCount = 0
            var lastEntry = ""
            while (isStreaming) {
                image = webcam.image
                if (image != null) {
                    try {
                        val result = MultiFormatReader().decode(BinaryBitmap(
                                HybridBinarizer(BufferedImageLuminanceSource(image))))
                        notFoundCount = 0
                        val resultText = result.text
                        if (resultText != lastEntry) {
                            Platform.runLater { onQRCodeResult(resultText) }
                            lastEntry = resultText
                        }
                    } catch (ignored: NotFoundException) {
                        notFoundCount++
                        if (notFoundCount > 3) {
                            Platform.runLater { onNoQRCodeFound() }
                            lastEntry = ""
                            notFoundCount = 0
                        }
                    }

                    imgRef.set(SwingFXUtils.toFXImage(image, imgRef.get()))
                    image.flush()
                    Platform.runLater { imageProperty.set(imgRef.get()) }
                }
            }
            webcam.close()
        }
        thread.isDaemon = false
        thread.start()
        streamImageView.imageProperty().bind(imageProperty)
    }

    private fun onQRCodeResult(result: String) {
        resultLabel.style = "-fx-background-color: lightgreen;-fx-padding: 10;"
        try {
            val entry: V5Entry = DecodedEntry(result)
            resultProperty.set("""
Match: ${entry.match}
Team: ${entry.team}
Scout: ${entry.scout}
Board: ${entry.board}""".trim())
            if (result !in previousEntries) {
                scannerEntries.add(DecodedEntry(result))
                previousEntries.add(result)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            resultProperty.set("Error in decoding entry: ${e.message}")
        }
    }

    private fun onNoQRCodeFound() {
        resultLabel.style = "\n-fx-padding: 10;\n-fx-background-color: #ddd;\n"
    }*/
}
