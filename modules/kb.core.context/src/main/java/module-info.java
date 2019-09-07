module kb.core.context {

    requires kotlin.stdlib;
    requires kb.service.api;

    uses kb.service.api.Service;
    uses kb.service.api.TextEditorProvider;

    exports kb.core.context;
}