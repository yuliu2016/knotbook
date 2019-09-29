module kb.application {

    requires kotlin.stdlib;

    requires kb.service.api;

    uses kb.service.api.application.ApplicationService;
    uses kb.service.api.Service;
    uses kb.service.api.TextEditorService;

    exports kb.application;
}