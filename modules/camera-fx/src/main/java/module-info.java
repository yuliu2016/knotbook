import kb.core.camera.fx.FXCameraService;

module kb.core.camera.fx {
    requires com.google.zxing;
    requires webcam.capture;
    requires javafx.controls;
    requires java.desktop;

    requires kb.service.api;

    provides kb.service.api.Service
            with FXCameraService;

    exports kb.core.camera.fx;
}