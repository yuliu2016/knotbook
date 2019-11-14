module kb.service.api {
    requires static org.kordamp.iconli.core;
    requires static javafx.controls;
    requires static java.desktop;
    requires java.logging;
    requires org.json;

    exports kb.service.api;
    exports kb.service.api.application;
    exports kb.service.api.array;
    exports kb.service.api.ui;
    exports kb.service.api.json;
}