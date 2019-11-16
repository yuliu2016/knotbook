module kb.plugin.scoutingapp {
    requires kb.service.api;
    requires kb.core.camera.fx;
    requires kotlin.stdlib;
    requires kb.core.fx;
    requires javafx.controls;

    provides kb.service.api.Service with kb.plugin.appscanner.AppScannerPlugin;

    exports kb.plugin.appscanner;
}