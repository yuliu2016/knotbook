package kb.core.context;

import kb.service.tool.KBExtensionTool;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@SuppressWarnings("unused")
public class ApplicationContext {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static void launch(Runnable runnable) {
        Registry.INSTANCE.load();
        ServiceLoader<KBExtensionTool> loader = ServiceLoader.load(KBExtensionTool.class);
        loader.iterator();
        List<KBExtensionTool> tools = new ArrayList<>();
        for (KBExtensionTool tool : loader) {
            tools.add(tool);
        }
        if (runnable != null) {
            runnable.run();
        }
    }
}
