package kb.service.api.textedit;

import kb.service.api.MetaService;
import org.jetbrains.annotations.NotNull;

public interface TextEditorService extends MetaService {

    @NotNull
    TextEditor create();

    boolean checkSyntaxSupport(@NotNull String syntax);
}
