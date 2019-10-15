package kb.service.api.util;

import javafx.application.Platform;

import javax.swing.*;

@SuppressWarnings("unused")
public class Util {
    public void runOnFxThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            try {
                Platform.runLater(runnable);
            } catch (IllegalStateException e) {
                Platform.startup(runnable);
            }
        }
    }

    public void runOnEDT(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }
}
