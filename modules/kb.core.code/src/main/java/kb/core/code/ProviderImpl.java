package kb.core.code;

import kb.service.api.TextEditor;
import kb.service.api.TextEditorProvider;
import org.jetbrains.annotations.NotNull;

public class ProviderImpl implements TextEditorProvider {
    @NotNull
    @Override
    public TextEditor create() {
        return new TextEditorImpl();
    }
}
