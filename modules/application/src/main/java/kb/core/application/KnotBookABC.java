package kb.core.application;

import kb.service.abc.ABC;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class KnotBookABC implements ABC {
    @Override
    public void launch(String[] args) {
        try {
            KnotBook.getKnotBook().launch(Arrays.asList(args));
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String trace = sw.toString();
            JOptionPane.showMessageDialog(null, trace,
                    "Error trying to start KnotBook ", JOptionPane.ERROR_MESSAGE);
        }
    }
}
