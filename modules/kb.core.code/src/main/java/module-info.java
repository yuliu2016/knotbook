module kb.core.code {
    requires java.desktop;
    requires annotations;

    requires rsyntaxtextarea;
    requires rstaui;

    requires kb.service.api;
    provides kb.service.api.TextEditorProvider
            with kb.core.code.ProviderImpl;

    exports kb.core.code;
}