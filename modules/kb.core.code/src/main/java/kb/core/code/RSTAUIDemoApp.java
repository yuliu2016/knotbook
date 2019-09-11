package kb.core.code;

import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.*;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

//import org.fife.rsta.ui.DocumentMap;


/**
 * An application that demonstrates use of the RSTAUI project.  Please don't
 * take this as good application design; it's just a simple example.<p>
 * <p>
 * Unlike the library itself, this class is public domain.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class RSTAUIDemoApp extends JFrame implements SearchListener {

    private CollapsibleSectionPanel csp;
    private RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
    private FindToolBar findToolBar;
    private ReplaceToolBar replaceToolBar;


    public RSTAUIDemoApp() {

        initSearchDialogs();

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        csp = new CollapsibleSectionPanel();
        contentPane.add(csp);

        setJMenuBar(createMenuBar());

        textArea = new RSyntaxTextArea(25, 80);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setMarkOccurrences(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        csp.add(sp);

        ErrorStrip errorStrip = new ErrorStrip(textArea);
        contentPane.add(errorStrip, BorderLayout.LINE_END);

        setTitle("RSTAUI Demo Application");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

    }


    private void addItem(Action a, ButtonGroup bg, JMenu menu) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
        bg.add(item);
        menu.add(item);
    }


    private JMenuBar createMenuBar() {

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Search");
        menu.add(new JMenuItem(new ShowFindDialogAction()));
        menu.add(new JMenuItem(new ShowReplaceDialogAction()));
        menu.add(new JMenuItem(new GoToLineAction()));
        menu.addSeparator();

        int ctrl = getToolkit().getMenuShortcutKeyMaskEx();
        int shift = InputEvent.SHIFT_DOWN_MASK;
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl | shift);
        Action a = csp.addBottomComponent(ks, findToolBar);
        a.putValue(Action.NAME, "Show Find Search Bar");
        menu.add(new JMenuItem(a));
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_H, ctrl | shift);
        a = csp.addBottomComponent(ks, replaceToolBar);
        a.putValue(Action.NAME, "Show Replace Search Bar");
        menu.add(new JMenuItem(a));

        mb.add(menu);

        menu = new JMenu("LookAndFeel");
        ButtonGroup bg = new ButtonGroup();
        LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo info : infos) {
            addItem(new LookAndFeelAction(info), bg, menu);
        }
        mb.add(menu);

        return mb;

    }


    @Override
    public String getSelectedText() {
        return textArea.getSelectedText();
    }


    /**
     * Creates our Find and Replace dialogs.
     */
    private void initSearchDialogs() {

        findDialog = new FindDialog(this, this);
        replaceDialog = new ReplaceDialog(this, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);

        // Create tool bars and tie their search contexts together also.
        findToolBar = new FindToolBar(this);
        findToolBar.setSearchContext(context);
        replaceToolBar = new ReplaceToolBar(this);
        replaceToolBar.setSearchContext(context);

    }


    /**
     * Listens for events from our search dialogs and actually does the dirty
     * work.
     */
    @Override
    public void searchEvent(SearchEvent e) {

        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result;

        switch (type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                SearchEngine.markAll(textArea, context);
                break;
            case FIND:
                result = SearchEngine.find(textArea, context);
                if (!result.wasFound()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(textArea, context);
                if (!result.wasFound()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE_ALL:
                result = SearchEngine.replaceAll(textArea, context);
                JOptionPane.showMessageDialog(null, result.getCount() +
                        " occurrences replaced.");
                break;
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//					UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RSTAUIDemoApp().setVisible(true);
        });
    }


    /**
     * Opens the "Go to Line" dialog.
     */
    private class GoToLineAction extends AbstractAction {

        GoToLineAction() {
            super("Go To Line...");
            int c = getToolkit().getMenuShortcutKeyMaskEx();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (findDialog.isVisible()) {
                findDialog.setVisible(false);
            }
            if (replaceDialog.isVisible()) {
                replaceDialog.setVisible(false);
            }
            GoToDialog dialog = new GoToDialog(RSTAUIDemoApp.this);
            dialog.setMaxLineNumberAllowed(textArea.getLineCount());
            dialog.setVisible(true);
            int line = dialog.getLineNumber();
            if (line > 0) {
                try {
                    textArea.setCaretPosition(textArea.getLineStartOffset(line - 1));
                } catch (BadLocationException ble) { // Never happens
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                    ble.printStackTrace();
                }
            }
        }

    }


    /**
     * Changes the Look and Feel.
     */
    private class LookAndFeelAction extends AbstractAction {

        private LookAndFeelInfo info;

        LookAndFeelAction(LookAndFeelInfo info) {
            putValue(NAME, info.getName());
            this.info = info;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                UIManager.setLookAndFeel(info.getClassName());
                SwingUtilities.updateComponentTreeUI(RSTAUIDemoApp.this);
                if (findDialog != null) {
                    findDialog.updateUI();
                    replaceDialog.updateUI();
                }
                pack();
            } catch (RuntimeException re) {
                throw re; // FindBugs
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Shows the Find dialog.
     */
    private class ShowFindDialogAction extends AbstractAction {

        ShowFindDialogAction() {
            super("Find...");
            int c = getToolkit().getMenuShortcutKeyMaskEx();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (replaceDialog.isVisible()) {
                replaceDialog.setVisible(false);
            }
            findDialog.setVisible(true);
        }

    }


    /**
     * Shows the Replace dialog.
     */
    private class ShowReplaceDialogAction extends AbstractAction {

        ShowReplaceDialogAction() {
            super("Replace...");
            int c = getToolkit().getMenuShortcutKeyMaskEx();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, c));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (findDialog.isVisible()) {
                findDialog.setVisible(false);
            }
            replaceDialog.setVisible(true);
        }

    }

}