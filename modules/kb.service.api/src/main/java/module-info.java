module kb.service.api {
    //noinspection JavaRequiresAutoModule
    requires transitive annotations;

    requires static org.kordamp.iconli.core;
    requires static javafx.controls;
    requires static java.desktop;

    exports kb.service.api;
    exports kb.service.api.df;
    exports kb.service.api.application;
    exports kb.service.api.table;
    exports kb.service.api.util;
}