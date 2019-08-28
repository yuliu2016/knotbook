module knotbook.application {
    requires javafx.graphics;

    requires knotbook.core.registry;
    requires knotbook.core.view;

    exports knotbook.application;
}