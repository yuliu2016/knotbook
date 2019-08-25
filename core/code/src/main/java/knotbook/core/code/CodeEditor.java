package knotbook.core.code;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.BorderPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;


@SuppressWarnings({"WeakerAccess", "unused"})
public class CodeEditor extends BorderPane {

    private RSyntaxTextArea area = new RSyntaxTextArea(36, 100);

    private StringProperty textProperty;

    public StringProperty textProperty() {
        if (textProperty == null) {
            textProperty = new SimpleStringProperty();
            textProperty.addListener((observable, oldValue, newValue) -> {
                area.setText(newValue);
            });
        }
        return textProperty;
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        textProperty().set(text);
    }

    private ObjectProperty<Syntax> syntaxProperty;

    public ObjectProperty<Syntax> syntaxProperty() {
        if (syntaxProperty == null) {
            syntaxProperty = new SimpleObjectProperty<>(Syntax.Plain);
            syntaxProperty.addListener((observable, oldValue, newValue) -> {
                area.setSyntaxEditingStyle(newValue.getValue());
            });
        }
        return syntaxProperty;
    }

    public Syntax getSyntax() {
        return syntaxProperty().get();
    }

    public void setSyntax(Syntax syntax) {
        syntaxProperty().set(syntax);
    }


    private CodeEditor() {

        JPanel cp = new JPanel(new BorderLayout());

        area.setCurrentLineHighlightColor(Color.white);
        area.setAntiAliasingEnabled(true);
        area.setCodeFoldingEnabled(true);
        area.setAutoIndentEnabled(true);
        area.setBracketMatchingEnabled(true);
        area.setPaintTabLines(true);
        area.setTabSize(4);
        area.setTabsEmulated(true);
        area.setCloseCurlyBraces(true);
        area.setMarkOccurrences(true);

        area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                Document document = e.getDocument();
                String text;
                try {
                    text = document.getText(0, document.getLength());
                } catch (BadLocationException ex) {
                    text = "";
                }
            }
        });

        RTextScrollPane sp = new RTextScrollPane(area);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        SwingNode swingNode = new SwingNode();
        swingNode.setContent(sp);
        setCenter(swingNode);
    }

    public static void launch() {
        Helper.runOnEDT(CodeEditor::new);
    }
}