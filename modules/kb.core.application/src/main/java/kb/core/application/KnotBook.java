package kb.core.application;

import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceMetadata;
import kb.service.api.TextEditorService;
import kb.service.api.application.ApplicationService;
import kb.service.api.application.JVMInstance;
import kb.service.api.application.PrivilegedContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

@SuppressWarnings("unused")
class KnotBook {

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
    private static final List<TextEditorService> textEditors = loadServices(TextEditorService.class);

    // App Registry
    private static final Registry registry = new Registry(new UserFile());

    static {
        print(apps);
        print(extensions);
        print(textEditors);
    }

    // App Context
    private static final PrivilegedContext context = new AppContextImpl(
            extensions,
            textEditors,
            registry
    );

    static void launch() {
        System.out.println(Arrays.toString(JVMInstance.getArgs()));

        for (ApplicationService app : apps) {
            app.launchFast();
            app.launch(context);
        }

        for (Service service : extensions) {
            service.launch(new ServiceContextImpl(service, context));
        }
    }
}
