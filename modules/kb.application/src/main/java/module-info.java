module kb.application {
    requires javafx.graphics;

    requires kb.core.view;
    requires kb.core.context;
    requires kb.service.api;

    uses kb.service.api.Service;
    uses kb.service.api.TextEditorProvider;

    exports kb.application;
}