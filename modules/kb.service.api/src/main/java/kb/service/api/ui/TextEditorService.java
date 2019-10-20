package kb.service.api.ui;

import kb.service.api.MetaService;
import org.jetbrains.annotations.NotNull;

public interface TextEditorService extends MetaService {

    @NotNull
    TextEditor create();

    boolean checkSyntaxSupport(@NotNull String syntax);
}
