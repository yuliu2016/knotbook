package kb.core.view.splash;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;

public class GCSplash {
    public static void splash() {
        Thread thread = new Thread(() -> {
            final Runtime runtime = Runtime.getRuntime();
            final long before = (runtime.totalMemory() - runtime.freeMemory());
            runtime.gc();
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
            Platform.runLater(() -> {
                long now = (runtime.totalMemory() - runtime.freeMemory());
                String mem = String.format("Currently Used Memory: %.3f MB", now / 1024.0 / 1024.0);
                String freed = String.format("Freed Memory: %.3f MB", (before - now) / 1024.0 / 1024.0);
                String msg = mem + "\n" + freed;
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Garbage Collection State");
                dialog.setContentText(msg);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                dialog.initStyle(StageStyle.UTILITY);
                dialog.show();
            });
        });
        thread.start();
    }
}
