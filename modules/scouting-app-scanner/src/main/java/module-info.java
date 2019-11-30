module kb.plugin.scoutingapp.scanner {
    requires kb.service.api;
    requires kb.core.camera.fx;
    requires kotlin.stdlib;
    requires kb.core.fx;
    requires kb.plugin.scoutingapp.api;
    requires javafx.controls;

    provides kb.service.api.Service with kb.plugin.scoutingapp.scanner.AppScannerPlugin;

    exports kb.plugin.scoutingapp.scanner;
}