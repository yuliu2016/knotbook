package kb.service.api.ui;

@FunctionalInterface
public interface TextEditorCallback {
    void onAction(boolean changed, String finalText);
}
