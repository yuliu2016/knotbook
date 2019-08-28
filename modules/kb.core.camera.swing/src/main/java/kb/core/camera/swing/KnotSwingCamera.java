package kb.core.camera.swing;

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


    public KnotSwingCamera() {
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
//        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(),0));
        Helper.runOnEDT(() -> {
            frame = new JFrame();
            imageView = new ImageView();
            frame.setTitle("Camera");
            frame.add(imageView);
        });
    }

    private static BufferedImage convertToARGB(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    private static BufferedImage createRotated(BufferedImage image) {
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.PI, image.getWidth() / 2f, image.getHeight() / 2.0);
        return createTransformed(image, at);
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
