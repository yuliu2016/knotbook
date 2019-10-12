package kb.service.api;

import org.jetbrains.annotations.NotNull;

public interface TextEditorService extends MetaService {

    @NotNull
    TextEditor create();

    boolean checkSyntaxSupport(@NotNull String syntax);
}
