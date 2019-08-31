package kb.core.context;

@SuppressWarnings("unused")
public class ApplicationContext {
    public static void launch(Runnable runnable) {
        Registry.INSTANCE.load();
        if (runnable != null) {
            runnable.run();
        }
    }
}
