module knotbook.main {
    requires kotlin.stdlib;
    requires javafx.graphics;
    requires javafx.controls;

    requires knotbook.core.snap;
    requires knotbook.core.splash;

    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;
    requires org.kordamp.ikonli.fontawesome5;

    exports knotbook.tables;
}