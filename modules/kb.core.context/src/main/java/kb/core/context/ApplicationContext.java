package kb.core.context;

@SuppressWarnings("unused")
public class ApplicationContext {
    public static void doAccess(Runnable runnable) {
        runnable.run();
    }
}
