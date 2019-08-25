package knotbook.core.code;

import javafx.application.Platform;

import javax.swing.*;

class Helper {
    static void runOnEDT(Runnable doRun) {
        if (SwingUtilities.isEventDispatchThread()) {
            doRun.run();
        } else {
            SwingUtilities.invokeLater(doRun);
        }
    }

    static void runOnFx(Runnable doRun) {
        if (Platform.isFxApplicationThread()) {
            doRun.run();
        } else {
            Platform.runLater(doRun);
        }
    }
}
