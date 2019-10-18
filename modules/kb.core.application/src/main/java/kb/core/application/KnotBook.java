package kb.core.application;

import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;
import kb.service.api.application.ApplicationService;
import kb.service.api.application.JVMInstance;
import kb.service.api.ui.TextEditorService;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
class KnotBook {

    private static class ResolvedServices<T extends MetaService> implements Iterable<T> {
        Class<T> theClass;
        List<T> theServices;

        ResolvedServices(Class<T> theClass, List<T> theServices) {
            this.theClass = theClass;
            this.theServices = theServices;
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return theServices.iterator();
        }

        void print() {
            System.out.println("\nListing " + theServices.size() +
                    " package(s) for " + theClass.getSimpleName() + ":");
            for (T s : theServices) {
                ServiceMetadata metadata = s.getMetadata();
                System.out.println(metadata.getPackageName() + " => " + metadata.getPackageVersion());
            }
        }
    }

    private static <T extends MetaService> ResolvedServices<T> loadServices(Class<T> service) {
        List<T> providers = new ArrayList<>();
        for (T provider : ServiceLoader.load(service)) {
            providers.add(provider);
        }
        return new ResolvedServices<>(service, providers);
    }


    // application
    private static final ResolvedServices<ApplicationService> applicationServices =
            loadServices(ApplicationService.class);

    // All extensions
    private static final ResolvedServices<Service> services =
            loadServices(Service.class);

    // Text Editor implementation
    private static final ResolvedServices<TextEditorService> textEditors =
            loadServices(TextEditorService.class);

    // App Registry
    private static final Registry registry = new Registry(new UserFile());

    // App Context
    private static final ServiceManagerImpl manager = new ServiceManagerImpl(
            services.theServices,
            textEditors.theServices,
            registry
    );

    private static Service serviceForApplication(ApplicationService app) {
        return new Service() {
            @Override
            public void launch(@NotNull ServiceContext context) {
            }

            @NotNull
            @Override
            public ServiceMetadata getMetadata() {
                return app.getMetadata();
            }
        };
    }

    private static ServiceContext contextForService(Service service) {
        return new ServiceContextImpl(service, manager);
    }

    static void launch() {
        System.out.println(Arrays.toString(JVMInstance.getArgs()));

        applicationServices.print();
        services.print();
        textEditors.print();

        for (ApplicationService app : applicationServices) {
            app.launchFast();
            app.launch(manager, contextForService(serviceForApplication(app)));
        }

        for (Service service : services) {
            service.launch(contextForService(service));
        }
    }
}
