package kb.application;

import javafx.application.Platform;
import kb.service.api.*;
import kb.service.api.application.ApplicationService;
import kb.service.api.application.JVMInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

@SuppressWarnings("unused")
class Application {

    private static <T extends MetaService> List<T> loadServices(Class<T> service) {
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

    // application
    private static final List<ApplicationService> apps = loadServices(ApplicationService.class);

    // All extensions
    private static final List<Service> extensions = loadServices(Service.class);

    // Text Editor implementation
    private static final List<TextEditorProvider> textEditors = loadServices(TextEditorProvider.class);

    // App Registry
    private static final Registry registry = new Registry();

    static void launch(Runnable runnable) {
        if (runnable != null) {
            System.out.println(Arrays.toString(JVMInstance.getArgs()));
            print(apps);
            print(extensions);
            print(textEditors);
            Platform.startup(runnable);
        }
    }
}
