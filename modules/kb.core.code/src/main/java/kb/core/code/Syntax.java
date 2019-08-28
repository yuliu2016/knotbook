package kb.core.code;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public enum Syntax {

    Java(SyntaxConstants.SYNTAX_STYLE_JAVA),

    Python(SyntaxConstants.SYNTAX_STYLE_PYTHON),

    Properties(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE),

    Plain(SyntaxConstants.SYNTAX_STYLE_NONE);

    private String value;

    public String getValue() {
        return value;
    }

    Syntax(String value) {
        this.value = value;
    }
}
