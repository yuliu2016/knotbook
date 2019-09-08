module kb.core.code {
    requires rsyntaxtextarea;
    requires java.desktop;
    requires annotations;

    requires kb.service.api;
    provides kb.service.api.TextEditorProvider
            with kb.core.code.ProviderImpl;

    exports kb.core.code;
}