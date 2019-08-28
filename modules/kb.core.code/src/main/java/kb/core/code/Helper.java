package kb.core.code;

import javax.swing.*;

class Helper {
    static void runOnEDT(Runnable doRun) {
        if (SwingUtilities.isEventDispatchThread()) {
            doRun.run();
        } else {
            SwingUtilities.invokeLater(doRun);
        }
    }
}
