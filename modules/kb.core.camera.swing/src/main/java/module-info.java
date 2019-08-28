module kb.core.camera.swing {
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires webcam.capture;
    requires javafx.controls;
    requires java.desktop;

    requires kb.core.splash;

    exports knotbook.core.swingcam;
}