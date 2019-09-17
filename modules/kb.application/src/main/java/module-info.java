module kb.application {
    requires javafx.base;
    requires javafx.graphics;

    requires kotlin.stdlib;
    requires org.apache.commons.lang3;

    requires kb.core.view;
    requires kb.service.api;

    uses kb.service.api.application.ApplicationService;
    uses kb.service.api.Service;
    uses kb.service.api.TextEditorProvider;

    exports kb.application;
}