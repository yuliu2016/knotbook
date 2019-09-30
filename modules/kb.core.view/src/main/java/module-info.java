module kb.core.view {
    requires kotlin.stdlib;
    requires javafx.graphics;
    requires javafx.controls;

    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.materialdesign;

    requires kb.service.api;
    requires kb.core.splash;
    requires kb.core.bowline;
    requires kb.core.fx;
    requires kb.core.icon;

    provides kb.service.api.application.ApplicationService
            with kb.core.view.Application;

    exports kb.core.view;
}