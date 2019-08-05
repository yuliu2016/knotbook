module knotbook.snap {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens borderless to javafx.fxml;
    exports borderless;
}