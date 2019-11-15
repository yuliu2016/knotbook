import kb.service.api.ui.TextEditorService;

module kb.core.application {

    requires java.desktop;
    requires org.json;

    requires kb.service.abc;
    provides kb.service.abc.ABC with kb.core.application.KnotBookABC;

    requires kb.service.api;

    uses kb.service.api.application.ApplicationService;
    uses kb.service.api.Service;
    uses TextEditorService;

    exports kb.core.application;
}