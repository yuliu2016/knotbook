package kb.application;

import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceMetadata;
import kb.service.api.TextEditorProvider;
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
    private static final Registry registry = new Registry(new UserFile());

    static void launch() {
        System.out.println(Arrays.toString(JVMInstance.getArgs()));
        print(apps);
        print(extensions);
        print(textEditors);
        for (ApplicationService app : apps) {
            app.launchFast();
        }
    }
}
