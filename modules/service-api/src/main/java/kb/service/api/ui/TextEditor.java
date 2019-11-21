package kb.service.api.ui;


@SuppressWarnings("unused")
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

    TextEditor addAction(String name, TextEditorCallback action);

    boolean isDarkTheme();

    void setDarkTheme(boolean darkTheme);

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

    default TextEditor withDarkTheme(boolean darkTheme) {
        setDarkTheme(darkTheme);
        return this;
    }
}
