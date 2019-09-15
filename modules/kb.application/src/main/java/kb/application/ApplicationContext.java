package kb.application;

import javafx.application.Platform;
import kb.core.context.Registry;
import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceMetadata;
import kb.service.api.TextEditorProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@SuppressWarnings("unused")
class ApplicationContext {

    private static <T extends MetaService> List<T> load(Class<T> service) {
        List<T> providers = new ArrayList<>();
        for (T provider : ServiceLoader.load(service)) {
            providers.add(provider);
        }
        return providers;
    }

    private static <T extends MetaService> void print(List<T> services) {
        System.out.println("\nListing " + services.size() + " package(s):");
        for (T s : services) {
            ServiceMetadata metadata = s.getMetadata();
            System.out.println(metadata.getPackageName() + " => " + metadata.getPackageVersion());
        }
    }

    static {
        Registry.INSTANCE.load();
    }

    // All extensions
    private static final List<Service> extensions = load(Service.class);

    // Text Editor implementation
    private static final List<TextEditorProvider> textEditors = load(TextEditorProvider.class);


    static void launch(Runnable runnable) {
        if (runnable != null) {
            print(extensions);
            print(textEditors);
            Platform.startup(runnable);
        }
    }
}
