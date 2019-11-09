module kb.core.view {
    requires kotlin.stdlib;
    requires jdk.httpserver;

    requires kb.service.api;
    requires kb.core.fx;
    requires kb.core.icon;

    requires org.controlsfx.controls;

    requires org.kordamp.ikonli.materialdesign;

    provides kb.service.api.application.ApplicationService
            with kb.core.view.app.Application;
}