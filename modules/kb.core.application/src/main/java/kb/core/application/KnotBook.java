package kb.core.application;

import kb.service.api.*;
import kb.service.api.application.ApplicationProps;
import kb.service.api.application.ApplicationService;
import kb.service.api.application.ServiceManager;
import kb.service.api.ui.CommandManager;
import kb.service.api.ui.Notification;
import kb.service.api.ui.TextEditor;
import kb.service.api.ui.TextEditorService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

@SuppressWarnings("unused")
class KnotBook implements ServiceManager {

    public interface RegistryHandle {
        InputStream input() throws IOException;

        OutputStream output() throws IOException;
    }

    private static class FileRegistryHandle implements RegistryHandle {
        File file;

        FileRegistryHandle(File file) {
            this.file = file;
        }

        @Override
        public InputStream input() throws IOException {
            return new FileInputStream(file);
        }

        @Override
        public OutputStream output() throws IOException {
            return new FileOutputStream(file);
        }
    }

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

    private static class ContextImpl implements ServiceContext {

        Service service;
        ApplicationService app;

        ContextImpl(Service service, ApplicationService app) {
            this.service = service;
            this.app = app;
        }

        @Override
        public Service getService() {
            return service;
        }

        @Override
        public ServiceProps getProps() {
            return getKnotBook().getProps().getProps(service.getMetadata().getPackageName());
        }

        @Override
        public TextEditor createTextEditor() {
            return getKnotBook().createTextEditor();
        }

        @Override
        public Notification createNotification() {
            return app.createNotification();
        }

        @Override
        public CommandManager getCommandManager() {
            return app.getCommandManager();
        }
    }

    private static <T extends MetaService> ResolvedServices<T> loadServices(Class<T> service) {
        List<T> providers = new ArrayList<>();
        for (T provider : ServiceLoader.load(service)) {
            providers.add(provider);
        }
        return new ResolvedServices<>(service, providers);
    }

    private static Service serviceForApplication(ServiceMetadata metadata) {
        return new MetadataServiceWrapper(metadata);
    }

    private static ServiceContext contextForService(Service service, ApplicationService app) {
        return new ContextImpl(service, app);
    }

    private KnotBook() {
    }

    private static final KnotBook theKnotBook = new KnotBook();

    static KnotBook getKnotBook() {
        return theKnotBook;
    }

    private String home = System.getProperty("user.home").replace(File.separatorChar, '/');
    private List<String> args;

    private boolean isDebug() {
        return args == null || args.contains("debug");
    }

    private RegistryHandle getHandle() {
        if (isDebug()) {
            return new FileRegistryHandle(new File(home, "knotbook.properties"));
        }
        return new FileRegistryHandle(new File(home, "knotbook-release.properties"));
    }

    private final ResolvedServices<ApplicationService> applications =
            loadServices(ApplicationService.class);
    private final ResolvedServices<Service> extensions =
            loadServices(Service.class);
    private final ResolvedServices<TextEditorService> textEditors =
            loadServices(TextEditorService.class);
    private final Registry2 registry = new Registry2(getHandle());

    void launch(List<String> args) {
        this.args = args;
        if (!applications.services.isEmpty()) {
            ApplicationService app = applications.services.get(0);
            launch(app);
        }
    }

    private void launch(ApplicationService app) {
        System.out.println(args);
        applications.print();
        extensions.print();
        textEditors.print();
        app.launch(getKnotBook(), contextForService(serviceForApplication(app.getMetadata()), app));
        for (Service service : extensions.services) {
            service.launch(contextForService(service, app));
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

    private TextEditor createTextEditor() {
        if (!textEditors.services.isEmpty()) {
            return textEditors.services.get(0).create();
        }
        throw new NoSuchElementException();
    }
}
