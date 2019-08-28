module kb.core.camera.fx {
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires webcam.capture;
    requires javafx.controls;
    requires java.desktop;

    requires kb.core.splash;

    exports kb.core.camera.fx;
}