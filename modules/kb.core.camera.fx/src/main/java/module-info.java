module kb.core.camera.fx {
    requires com.google.zxing;
    requires webcam.capture;
    requires javafx.controls;
    requires java.desktop;

    requires annotations;
    requires kb.service.api;

    provides kb.service.api.Service
            with kb.core.camera.fx.KBCameraService;

    exports kb.core.camera.fx;
}