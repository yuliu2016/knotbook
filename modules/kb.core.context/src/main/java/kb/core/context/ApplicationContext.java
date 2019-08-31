package kb.core.context;

@SuppressWarnings("unused")
public class ApplicationContext {
    public static void obtainRoot(Runnable runnable) {
        runnable.run();
    }
}
