package kb.core.view.splash;

import javafx.application.Platform;
import kb.core.view.app.Singleton;

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
                Singleton.INSTANCE
                        .getContext()
                        .createNotification()
                        .setMessage(msg)
                        .show();
            });
        });
        thread.start();
    }
}
