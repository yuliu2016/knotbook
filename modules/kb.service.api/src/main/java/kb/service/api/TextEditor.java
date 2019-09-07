package kb.service.api;

public interface TextEditor {

    String getSyntax();

    void setSyntax();

    void setInitialText();

    String getFinalText();

    void show();

    boolean isFileChanged();

    void setTitle(String title);

    void addAction(String name, Runnable action);
}
