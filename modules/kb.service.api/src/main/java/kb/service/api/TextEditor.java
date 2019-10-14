package kb.service.api;

import org.jetbrains.annotations.NotNull;

public interface TextEditor {

    String getSyntax();

    void setSyntax(String syntax);

    void setInitialText(String text);

    void setEditable(boolean editable);

    String getFinalText();

    void show();

    boolean isTextChanged();

    String getTitle();

    void setTitle(String title);

    void addAction(@NotNull String name, @NotNull Runnable action);
}
