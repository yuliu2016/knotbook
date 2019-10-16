import kb.service.api.textedit.TextEditorService;

module kb.core.code {
    requires java.desktop;

    requires rsyntaxtextarea;

    requires kb.service.api;
    provides TextEditorService
            with kb.core.code.ProviderImpl;

    exports kb.core.code;
}