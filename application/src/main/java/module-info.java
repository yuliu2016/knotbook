module knotbook.application {
    requires kotlin.stdlib;
    requires javafx.graphics;
    requires javafx.controls;

    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.fontawesome5;

    requires knotbook.core.camera;
    requires knotbook.core.splash;
    requires knotbook.core.table;
    requires knotbook.core.fx;
    requires knotbook.core.registry;
    requires knotbook.core.icon;
    requires knotbook.core.server;
    requires knotbook.core.code;

    requires knotbook.pathplanner;

    exports knotbook.application;
}