package kb.core.application;

import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;
import kb.service.api.application.ApplicationProps;
import kb.service.api.application.ApplicationService;
import kb.service.api.application.ServiceManager;
import kb.service.api.json.JSONObjectWrapper;
import kb.service.api.ui.TextEditor;
import kb.service.api.ui.TextEditorService;
import kb.service.api.ui.UIManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unused")
class KnotBook {

    public interface ConfigHandle {
        String read();

        void write(String s);

        Path getPath();
    }

    private static class FileConfigHandle implements ConfigHandle {
        Path path;

        FileConfigHandle(Path path) {
            this.path = path;
        }

        @Override
        public String read() {
            try {
                return Files.readString(path);
            } catch (IOException e) {
                e.printStackTrace();
                return "{}";
            }
        }

        @Override
        public void write(String s) {
            try {
                Files.writeString(path, s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Path getPath() {
            return path;
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
        public JSONObjectWrapper getConfig() {
            return getKnotBook().config.getConfig(service);
        }

        @Override
        public TextEditor createTextEditor() {
            return getKnotBook().createTextEditor();
        }

        @Override
        public UIManager getUIManager() {
            return app.getUIManager();
        }
    }

    private static class Manager implements ServiceManager {

        @Override
        public ApplicationProps getProps() {
            return getKnotBook().config;
        }

        @Override
        public List<Service> getServices() {
            return getKnotBook().extensions.services;
        }

        @Override
        public String getVersion() {
            if (getKnotBook().isDebug()) {
                Date date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int minor;
                if (year == 2019) {
                    minor = month - 8;
                } else {
                    minor = 4 + month + (year - 2020) * 12;
                }
                return "3." + minor + "." + day + "-dev";
            }
            return "3.3.19";
        }

        @Override
        public void exitOK() {
            for (Service service : getServices()) {
                service.terminate();
            }
            getKnotBook().config.save();
            System.exit(0);
        }
    }

    private static <T extends MetaService> ResolvedServices<T> loadServices(
            ServiceLoader<T> loader, Class<T> theClass) {
        List<T> providers = new ArrayList<>();
        for (T provider : loader) {
            providers.add(provider);
        }
        return new ResolvedServices<>(theClass, providers);
    }

    private static Service serviceForApplication(ServiceMetadata metadata) {
        return new MetadataServiceWrapper(metadata);
    }

    private static ServiceContext contextForService(Service service, ApplicationService app) {
        return new ContextImpl(service, app);
    }

    private static void launchPlugin(Service service, ApplicationService app) {
        if (service.isAvailable()) {
            try {
                service.launch(contextForService(service, app));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private KnotBook() {
    }

    private static final KnotBook theKnotBook = new KnotBook();

    static KnotBook getKnotBook() {
        return theKnotBook;
    }

    private List<String> args;
    private String launcherPath = System.getProperty("java.launcher.path");
    private String home = System.getProperty("user.home");

    private boolean isDebug() {
        return args != null && args.contains("debug");
    }

    private ConfigHandle getHandle() {
        if (isDebug()) {
            return new FileConfigHandle(Paths.get(home, "knotbook-config-debug.json"));
        }
        if (launcherPath != null) {
            return new FileConfigHandle(Paths.get(launcherPath, "app", "config.json"));
        }
        throw new IllegalStateException();
    }

    private final ResolvedServices<ApplicationService> applications =
            loadServices(ServiceLoader.load(ApplicationService.class), ApplicationService.class);
    private final ResolvedServices<Service> extensions =
            loadServices(ServiceLoader.load(Service.class), Service.class);
    private final ResolvedServices<TextEditorService> textEditors =
            loadServices(ServiceLoader.load(TextEditorService.class), TextEditorService.class);

    private Config config;
    private Manager manager;

    void launch(List<String> args) {
        this.args = args;
        config = new Config(getHandle());
        manager = new Manager();
        if (!applications.services.isEmpty()) {
            ApplicationService app = applications.services.get(0);
            launch(app);
        }
    }

    private void launch(ApplicationService app) {
        applications.print();
        extensions.print();
        textEditors.print();
        app.launch(manager, contextForService(serviceForApplication(app.getMetadata()), app), () -> {
            for (Service service : extensions.services) launchPlugin(service, app);
        });
    }

    private TextEditor createTextEditor() {
        if (!textEditors.services.isEmpty()) {
            return textEditors.services.get(0).create();
        }
        throw new NoSuchElementException();
    }
}
