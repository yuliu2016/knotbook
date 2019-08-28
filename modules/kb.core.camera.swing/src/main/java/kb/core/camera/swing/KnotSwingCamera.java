package kb.core.camera.swing;

import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

@SuppressWarnings("WeakerAccess")
public class KnotSwingCamera {


    private static class ImageView extends JPanel {

        private Image image;

        public void setImage(Image image) {
            this.image = image;
            Helper.runOnEDT(this::repaint);
        }

        @Override
        public void paint(Graphics g) {
            super.paintComponent(g);
            g.drawRect(0, 0, getWidth(), getHeight());
            g.drawImage(image, 0, 0, null);
        }
    }

    private JFrame frame;
    private ImageView imageView;

    AffineTransform at = new AffineTransform();

    Webcam webcam = Webcam.getDefault();

    public KnotSwingCamera() {
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
//        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(),0));
        Helper.runOnEDT(() -> {
            frame = new JFrame();
            imageView = new ImageView();
            frame.setTitle("Camera");
            frame.add(imageView);
            Thread thread = new Thread(() -> {
                BufferedImage image = webcam.getImage();
                BufferedImage transformed = createTransformed(image, at);
                Helper.runOnEDT(() -> imageView.setImage(transformed));
            });
            thread.start();
        });
    }

    private static BufferedImage createTransformed(
            BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
