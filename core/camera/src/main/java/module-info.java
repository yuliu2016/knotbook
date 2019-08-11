module knotbook.core.camera {
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires webcam.capture;
    requires javafx.controls;
    requires javafx.swing;
    requires java.desktop;

    requires knotbook.core.splash;

    exports knotbook.core.camera;
}