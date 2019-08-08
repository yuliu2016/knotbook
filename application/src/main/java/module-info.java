module knotbook.application {
    requires kotlin.stdlib;
    requires javafx.graphics;
    requires javafx.controls;

    requires knotbook.core.snap;
    requires knotbook.core.splash;
    requires knotbook.core.table;
    requires knotbook.core.fx;
    requires knotbook.core.registry;

    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;
    requires org.kordamp.ikonli.fontawesome5;

    exports knotbook.application;
}