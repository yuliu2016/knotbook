package kb.service.api.ui;

import javafx.application.Platform;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class UIHelper {
    public static String getStackTrace(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        return writer.toString();
    }

    public static Executor createExecutor(String name, Consumer<Throwable> exceptionHandler) {
        return Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("KnotBook " + name);
            thread.setUncaughtExceptionHandler((t, e) -> {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            });
            return thread;
        });
    }

    public static void run(Runnable func) {
        if (Platform.isFxApplicationThread()) {
            func.run();
        } else {
            Platform.runLater(func);
        }
    }
}
