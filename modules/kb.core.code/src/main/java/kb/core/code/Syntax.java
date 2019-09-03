package kb.core.code;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

@SuppressWarnings("unused")
public enum Syntax {

    Plain(SyntaxConstants.SYNTAX_STYLE_NONE),

    Properties(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE),

    Java(SyntaxConstants.SYNTAX_STYLE_JAVA),

    Python(SyntaxConstants.SYNTAX_STYLE_PYTHON),

    XML(SyntaxConstants.SYNTAX_STYLE_XML),

    JSON(SyntaxConstants.SYNTAX_STYLE_JSON),

    C(SyntaxConstants.SYNTAX_STYLE_C),

    CPP(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS),

    CSS(SyntaxConstants.SYNTAX_STYLE_CSS),

    LESS(SyntaxConstants.SYNTAX_STYLE_LESS),

    CSV(SyntaxConstants.SYNTAX_STYLE_CSV);

    private String value;

    public String getValue() {
        return value;
    }

    Syntax(String value) {
        this.value = value;
    }
}
