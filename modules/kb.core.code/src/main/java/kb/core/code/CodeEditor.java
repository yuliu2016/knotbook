package kb.core.code;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

/**
 * Code editor in Swing using {@link RSyntaxTextArea},
 * however, every public method should be called from the JavaFX
 * application thread
 */

@SuppressWarnings({"unused"})
public class CodeEditor {

    private RSyntaxTextArea area = new RSyntaxTextArea(30, 84);
    private JFrame frame = new JFrame();

    public CodeEditor(String title, boolean editable, String yes, String no, String initialText,
                      Consumer<String> yesRun, Syntax syntax) {
        Helper.runOnEDT(() -> {

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JPanel cp = new JPanel();

            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            area.setSyntaxEditingStyle(syntax.getValue());
            area.setCurrentLineHighlightColor(Color.white);
            area.setCodeFoldingEnabled(true);
            area.setAutoIndentEnabled(true);
            area.setPaintTabLines(true);
            area.setTabSize(4);
            area.setTabsEmulated(true);
            area.setCloseCurlyBraces(true);
            area.setFont(area.getFont().deriveFont(15f));
            area.setText(initialText);
            area.setEditable(editable);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);

            RTextScrollPane sp = new RTextScrollPane(area);
            sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            if (editable) {
                var bottomPanel = new JPanel();
                bottomPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
                bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
                bottomPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
                bottomPanel.setBorder(null);

                var okBtn = new JButton(no);
                okBtn.setAlignmentX(JButton.RIGHT_ALIGNMENT);
                okBtn.setBackground(new Color(224, 224, 224));
                okBtn.addActionListener(e -> frame.dispose());

                var closeBtn = new JButton(yes);
                closeBtn.setBackground(new Color(224, 224, 255));
                closeBtn.setAlignmentX(JButton.RIGHT_ALIGNMENT);
                closeBtn.addActionListener(e -> {
                    frame.dispose();
                    yesRun.accept(area.getText());
                });

                bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
                bottomPanel.add(okBtn);
                bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
                bottomPanel.add(closeBtn);
                bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));

                cp.add(Box.createRigidArea(new Dimension(0, 5)));
                cp.add(bottomPanel);
                cp.add(Box.createRigidArea(new Dimension(0, 5)));
            }

            cp.add(sp);

            frame.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        frame.dispose();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            frame.setContentPane(cp);
            frame.setTitle(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            area.requestFocusInWindow();
        });
    }
}