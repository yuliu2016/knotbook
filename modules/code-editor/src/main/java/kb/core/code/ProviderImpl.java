package kb.core.code;

import kb.service.api.ServiceMetadata;
import kb.service.api.ui.TextEditor;
import kb.service.api.ui.TextEditorService;

public class ProviderImpl implements TextEditorService {

    private ServiceMetadata metadata = new ServiceMetadata();

    public ProviderImpl() {
        metadata.setPackageName("text.editor");
        metadata.setPackageVersion("1.0");
    }


    @Override
    public ServiceMetadata getMetadata() {
        return metadata;
    }


    @Override
    public TextEditor create() {
        return new TextEditorImpl();
    }

    @Override
    public boolean checkSyntaxSupport(String syntax) {
        for (Syntax s : Syntax.values()) {
            if (s.name().equals(syntax)) {
                return true;
            }
        }
        return false;
    }
}
