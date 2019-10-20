package kb.core.code;

import kb.service.api.ServiceMetadata;
import kb.service.api.ui.TextEditor;
import kb.service.api.ui.TextEditorService;
import org.jetbrains.annotations.NotNull;

public class ProviderImpl implements TextEditorService {

    private ServiceMetadata metadata = new ServiceMetadata();

    public ProviderImpl() {
        metadata.setPackageName("rsyn.text.editor");
    }

    @NotNull
    @Override
    public ServiceMetadata getMetadata() {
        return metadata;
    }

    @NotNull
    @Override
    public TextEditor create() {
        return new TextEditorImpl();
    }

    @Override
    public boolean checkSyntaxSupport(@NotNull String syntax) {
        for (Syntax s : Syntax.values()) {
            if (s.name().equals(syntax)) {
                return true;
            }
        }
        return false;
    }
}
