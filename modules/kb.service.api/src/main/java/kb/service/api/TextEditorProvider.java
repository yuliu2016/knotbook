package kb.service.api;

import org.jetbrains.annotations.NotNull;

public interface TextEditorProvider {
    @NotNull
    TextEditor create();
}
