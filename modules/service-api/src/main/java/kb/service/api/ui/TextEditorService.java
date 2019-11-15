package kb.service.api.ui;

import kb.service.api.MetaService;

public interface TextEditorService extends MetaService {

    TextEditor create();

    boolean checkSyntaxSupport(String syntax);
}
