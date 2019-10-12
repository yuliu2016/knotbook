import kb.service.api.TextEditorService;

module kb.core.code {
    requires java.desktop;
    requires annotations;

    requires rsyntaxtextarea;

    requires kb.service.api;
    provides TextEditorService
            with kb.core.code.ProviderImpl;

    exports kb.core.code;
}