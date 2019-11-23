package kb.core.application;

import kb.service.abc.ABC;
import kb.service.api.ui.UIHelper;

import javax.swing.*;
import java.util.Arrays;

public class KnotBookABC implements ABC {
    @Override
    public void launch(String[] args) {
        try {
            KnotBook.getKnotBook().launch(Arrays.asList(args));
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, UIHelper.getStackTrace(e),
                    "Error trying to start KnotBook ", JOptionPane.ERROR_MESSAGE);
        }
    }
}
