package kb.core.code;

import kb.service.api.TextEditor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

class TextEditorImpl implements TextEditor {

    private String syntax = SyntaxConstants.SYNTAX_STYLE_NONE;
    private String initialText = "";
    private boolean editable = false;
    private String title = "Text Editor";
    private boolean textChanged = false;
    private String finalText = null;

    static class Action {
        String name;
        Runnable doRun;

        Action(String name, Runnable doRun) {
            this.name = name;
            this.doRun = doRun;
        }
    }

    private List<Action> actions = new ArrayList<>();

    @Override
    public String getSyntax() {
        return syntax;
    }

    @Override
    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    @Override
    public void setInitialText(String text) {
        initialText = text;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String getFinalText() {
        return finalText;
    }

    @Override
    public boolean isTextChanged() {
        return textChanged;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void show() {
        Helper.runOnEDT(this::showImpl);
    }

    @Override
    public void addAction(@NotNull String name, @NotNull Runnable action) {
        actions.add(new Action(name, action));
    }

    private void showImpl() {
        JFrame frame = new JFrame();
        RSyntaxTextArea area = new RSyntaxTextArea(35, 84);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel cp = new JPanel();

        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

        area.setSyntaxEditingStyle(syntax);
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

            bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
            for (Action action : actions) {
                JButton button = new JButton(action.name);

                button.addActionListener(e -> {
                    finalText = area.getText();
                    if (!finalText.equals(initialText)) {
                        textChanged = true;
                    }
                    frame.dispose();
                    action.doRun.run();
                });

                bottomPanel.add(button);
                bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
            }

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
    }
}