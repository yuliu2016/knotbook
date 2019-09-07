package kb.service.api;

import org.jetbrains.annotations.NotNull;

public interface TextEditorProvider extends MetaService {

    @NotNull
    TextEditor create();

    boolean checkSyntaxSupport(@NotNull String syntax);
}
