module kb.core.camera.fx {
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires webcam.capture;
    requires javafx.controls;
    requires java.desktop;

    requires annotations;
    requires kb.service.api;
    requires kb.core.splash;

    provides kb.service.api.Service
            with kb.core.camera.fx.KBCameraService;

    exports kb.core.camera.fx;
}