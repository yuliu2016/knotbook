package kb.core.application;

import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;
import kb.service.api.application.ApplicationProps;
import kb.service.api.application.ApplicationService;
import kb.service.api.application.JVMInstance;
import kb.service.api.application.ServiceManager;
import kb.service.api.ui.TextEditor;
import kb.service.api.ui.TextEditorService;

import java.util.*;

@SuppressWarnings("unused")
class KnotBook implements ServiceManager {

    private static class ResolvedServices<T extends MetaService> {
        Class<T> theClass;
        List<T> services;

        ResolvedServices(Class<T> theClass, List<T> services) {
            this.theClass = theClass;
            this.services = services;
        }

        void print() {
            System.out.println("\nListing " + services.size() +
                    " package(s) for " + theClass.getSimpleName() + ":");
            for (T s : services) {
                ServiceMetadata metadata = s.getMetadata();
                System.out.println(metadata.getPackageName() + " => " + metadata.getPackageVersion());
            }
        }
    }

    private static class MetadataServiceWrapper implements Service {
        private ServiceMetadata metadata;

        MetadataServiceWrapper(ServiceMetadata metadata) {
            this.metadata = metadata;
        }

        @Override
        public void launch(ServiceContext context) {
        }

        @Override
        public ServiceMetadata getMetadata() {
            return metadata;
        }
    }

    private static <T extends MetaService> ResolvedServices<T> loadServices(Class<T> service) {
        List<T> providers = new ArrayList<>();
        for (T provider : ServiceLoader.load(service)) {
            providers.add(provider);
        }
        return new ResolvedServices<>(service, providers);
    }

    private static Service serviceForApplication(ApplicationService app) {
        return new MetadataServiceWrapper(app.getMetadata());
    }

    private KnotBook() {
    }

    private static final KnotBook theKnotBook = new KnotBook();

    static KnotBook getKnotBook() {
        return theKnotBook;
    }

    private final ResolvedServices<ApplicationService> applications =
            loadServices(ApplicationService.class);
    private final ResolvedServices<Service> extensions =
            loadServices(Service.class);
    private final ResolvedServices<TextEditorService> textEditors =
            loadServices(TextEditorService.class);
    private final Registry registry = new Registry(new UserFile());

    private static ServiceContext contextForService(Service service, ApplicationService app) {
        return new ServiceContextImpl(service, theKnotBook, app);
    }

    void launch() {
        System.out.println(Arrays.toString(JVMInstance.getArgs()));

        applications.print();
        extensions.print();
        textEditors.print();

        if (!applications.services.isEmpty()) {
            ApplicationService app = applications.services.get(0);
            app.launch(theKnotBook, contextForService(serviceForApplication(app), app));
            for (Service service : extensions.services) {
                service.launch(contextForService(service, app));
            }
        }
    }

    @Override
    public ApplicationProps getProps() {
        return registry;
    }

    @Override
    public List<Service> getServices() {
        return extensions.services;
    }

    @Override
    public String getVersion() {
        return "3.1.0";
    }

    @Override
    public void exit() {
        for (Service service : getServices()) {
            service.terminate();
        }
        registry.save();
        System.exit(0);
    }

    TextEditor createTextEditor() {
        if (!textEditors.services.isEmpty()) {
            return textEditors.services.get(0).create();
        }
        throw new NoSuchElementException();
    }
}
