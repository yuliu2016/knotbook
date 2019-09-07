package kb.core.context;

import kb.service.api.Service;
import kb.service.api.TextEditorProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public class ApplicationContext {

    private static <T> List<T> load(Class<T> service) {
        List<T> providers = new ArrayList<>();
        for (T provider : ServiceLoader.load(service)) {
            providers.add(provider);
        }
        return providers;
    }

    static {
        Registry.INSTANCE.load();
    }

    private static final List<Service> extensions = load(Service.class);
    private static final List<TextEditorProvider> textEditors = load(TextEditorProvider.class);


    public static void launch(Runnable runnable) {
        if (runnable != null) {
            System.out.println(extensions);
            System.out.println(textEditors);
            runnable.run();
        }
    }
}
