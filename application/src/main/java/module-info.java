module knotbook.application {
    requires kotlin.stdlib;
    requires javafx.graphics;
    requires javafx.controls;

    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.materialdesign;
    requires org.kordamp.ikonli.fontawesome5;

    requires knotbook.core.snappy;
    requires knotbook.core.splash;
    requires knotbook.core.table;
    requires knotbook.core.fx;
    requires knotbook.core.registry;
    requires knotbook.core.icon;

    exports knotbook.application;
}