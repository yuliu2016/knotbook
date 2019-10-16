import kb.service.api.textedit.TextEditorService;

module kb.core.application {

    requires kb.service.abc;
    provides kb.service.abc.ABC with kb.core.application.KnotBookABC;

    requires kb.service.api;

    requires kotlin.stdlib;

    uses kb.service.api.application.ApplicationService;
    uses kb.service.api.Service;
    uses TextEditorService;

    exports kb.core.application;
}