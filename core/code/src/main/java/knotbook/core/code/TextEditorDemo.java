package knotbook.core.code;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * A simple example showing how to use RSyntaxTextArea to add Java syntax
 * highlighting to a Swing application.
 */
public class TextEditorDemo{

   private TextEditorDemo() {

      JPanel cp = new JPanel(new BorderLayout());

      RSyntaxTextArea textArea = new RSyntaxTextArea(36, 100);
      textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
      textArea.setCurrentLineHighlightColor(Color.white);
      textArea.setFont(textArea.getFont().deriveFont(16f));
      textArea.setAntiAliasingEnabled(true);
      textArea.setCodeFoldingEnabled(true);

      RTextScrollPane sp = new RTextScrollPane(textArea);
      cp.add(sp);

      JFrame frame = new JFrame();

      frame.setContentPane(cp);
      frame.setTitle("Text Editor Demo");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

   }

   public static void launch() {
      // Start all Swing applications on the EDT.
      SwingUtilities.invokeLater(TextEditorDemo::new);
   }
}