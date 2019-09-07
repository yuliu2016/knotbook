package kb.core.context;

import kb.service.api.KBExtensionTool;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public class ApplicationContext {

    private static final List<KBExtensionTool> tools = new ArrayList<>();

    public static void launch(Runnable runnable) {
        Registry.INSTANCE.load();
        ServiceLoader<KBExtensionTool> loader = ServiceLoader.load(KBExtensionTool.class);
        loader.iterator();
        for (KBExtensionTool tool : loader) {
            tools.add(tool);
        }
        if (runnable != null) {
            runnable.run();
        }
    }
}
