package kb.service.api.textedit;

@FunctionalInterface
public interface TextEditorCallback {
    void onAction(boolean changed, String finalText);
}
