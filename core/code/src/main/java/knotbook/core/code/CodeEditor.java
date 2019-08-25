package knotbook.core.code;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;


@SuppressWarnings({"WeakerAccess", "unused"})
public class CodeEditor extends BorderPane {

    private RSyntaxTextArea area;
    private RTextScrollPane sp;

    private ObjectProperty<Syntax> syntaxProperty;

    public ObjectProperty<Syntax> syntaxProperty() {
        if (syntaxProperty == null) {
            syntaxProperty = new SimpleObjectProperty<>(Syntax.Plain);
            syntaxProperty.addListener((observable, oldValue, newValue) -> {
                // Apply the change on the EDT
                Helper.runOnEDT(() -> area.setSyntaxEditingStyle(newValue.getValue()));
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

    private SwingNode swingNode = new SwingNode();

    private CodeEditor() {
        super();
        setCenter(swingNode);

        InvalidationListener listener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                int width = (int) getWidth();
                int height = (int) getHeight();
                Helper.runOnEDT(() -> sp.setSize(width, height));
            }
        };

        widthProperty().addListener(listener);
        heightProperty().addListener(listener);

        Helper.runOnEDT(() -> {
            area = new RSyntaxTextArea(36, 100);
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
            area.setText("\n\nhi");

            sp = new RTextScrollPane(area);
            sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            swingNode.setContent(sp);

            Helper.runOnFx(() -> {

            });
        });
    }

    public static void launch() {
        Helper.runOnFx(() -> {
            Stage stage = new Stage();
            CodeEditor editor = new CodeEditor();
            stage.setTitle("Code Editor");
            stage.setScene(new Scene(editor));
            stage.show();
        });
    }
}