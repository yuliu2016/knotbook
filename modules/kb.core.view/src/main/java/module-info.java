module kb.core.view {
    requires kotlin.stdlib;
    requires javafx.graphics;
    requires javafx.controls;

    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.materialdesign;

    requires kb.core.camera.fx;
    requires kb.core.splash;
    requires kb.core.bowline;
    requires kb.core.fx;
    requires kb.core.icon;
    requires kb.core.server;
    requires kb.core.code;
    requires kb.core.context;

    requires kb.pathplanner;

    exports kb.core.view;
}