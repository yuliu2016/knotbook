package kb.service.api.textedit;

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

    TextEditor addAction(@NotNull String name, @NotNull TextEditorCallback action);

    default TextEditor withSyntax(String syntax) {
        setSyntax(syntax);
        return this;
    }

    default TextEditor withTitle(String title) {
        setTitle(title);
        return this;
    }

    default TextEditor editable() {
        setEditable(true);
        return this;
    }

    default TextEditor withInitialText(String initialText) {
        setInitialText(initialText);
        return this;
    }
}
