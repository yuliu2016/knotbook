package kb.core.application;

import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;
import kb.service.api.application.ApplicationProps;
import kb.service.api.application.ApplicationService;
import kb.service.api.application.ServiceManager;
import kb.service.api.data.DataSpace;
import kb.service.api.json.JSONObjectWrapper;
import kb.service.api.ui.TextEditor;
import kb.service.api.ui.TextEditorService;
import kb.service.api.ui.UIManager;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static class Config implements ApplicationProps {
        private JSONObject object;
        private ConfigHandle handle;

        Config(ConfigHandle handle) {
            this.handle = handle;
            try {
                object = new JSONObject(handle.read());
            } catch (JSONException e) {
                object = new JSONObject();
            }
        }

        void save() {
            handle.write(getJoinedText());
        }

        JSONObjectWrapper createConfig(Service service) {
            String key = service.getMetadata().getPackageName();
            JSONObject savedConfig = object.optJSONObject(key);
            JSONObjectWrapper newWrapper;
            if (savedConfig != null) {
                newWrapper = new JSONObjectWrapper(savedConfig);
            } else {
                JSONObject newObject = new JSONObject();
                object.put(key, newObject);
                newWrapper = new JSONObjectWrapper(newObject);
            }
            return newWrapper;
        }

        @Override
        public String getJoinedText() {
            return object.toString(2);
        }

        @Override
        public void setInputText(String inputText) {
            try {
                object = new JSONObject(inputText);
            } catch (JSONException e) {
                object = new JSONObject();
            }
        }

        @Override
        public Path getPath() {
            return handle.getPath();
        }
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
        JSONObjectWrapper configCache;

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
            if (configCache == null) {
                configCache = getKnotBook().config.createConfig(service);
            }
            return configCache;
        }

        @Override
        public TextEditor createTextEditor() {
            return getKnotBook().createTextEditor();
        }

        @Override
        public UIManager getUIManager() {
            return app.getUIManager();
        }

        @Override
        public DataSpace getDataSpace() {
            return app.getDataSpace();
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
        public String getBuildVersion() {
            return getKnotBook().getBuildVersion();
        }

        @Override
        public String getImageVersion() {
            return getKnotBook().getImageVersion();
        }

        @Override
        public void exitOK() {
            getKnotBook().exit();
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

    private static void launchService(Service service, ApplicationService app) {
        if (service.isAvailable()) {
            try {
                service.launch(contextForService(service, app));
            } catch (Exception e) {
                app.getUIManager().showException(e);
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
    private String home = System.getProperty("user.home");

    private boolean isDebug() {
        return args != null && args.contains("debug");
    }

    private ConfigHandle getHandle() {
        if (isDebug()) {
            return new FileConfigHandle(Paths.get(home, ".knotbook", "knotbook-config-debug.json"));
        }
        return new FileConfigHandle(Paths.get(home, ".knotbook", "knotbook-config-release.json"));
    }

    private final ResolvedServices<ApplicationService> applications =
            loadServices(ServiceLoader.load(ApplicationService.class), ApplicationService.class);

    private final ResolvedServices<Service> extensions =
            loadServices(ServiceLoader.load(Service.class), Service.class);

    private final ResolvedServices<TextEditorService> textEditors =
            loadServices(ServiceLoader.load(TextEditorService.class), TextEditorService.class);

    private Config config;
    private Manager manager;
    private ApplicationService app;
    private String buildVersion;
    private String imageVersion;

    void launch(List<String> args) {
        this.args = args;
        config = new Config(getHandle());
        manager = new Manager();
        if (!applications.services.isEmpty()) {
            app = applications.services.get(0);
            launchApplication();
        } else throw new IllegalStateException("No Application Found");
    }

    private void launchApplication() {
        applications.print();
        extensions.print();
        textEditors.print();
        ServiceContext context = contextForService(serviceForApplication(app.getMetadata()), app);
        app.launch(manager, context, this::launchServices);
    }

    private void launchServices() {
        for (Service service : extensions.services) {
            launchService(service, app);
        }
    }

    private String getBuildVersion() {
        if (buildVersion == null) {
            updateVersion();
        }
        return buildVersion;
    }

    private String getImageVersion() {
        if (imageVersion == null) {
            updateVersion();
        }
        return imageVersion;
    }

    private void updateVersion() {
        if (isDebug()) {
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            updateBuildVersion(year, month, day);
            buildVersion += "-dev";
        } else {
            try {
                String json = Files.readString(Paths.get(getKnotBook().home, ".knotbook",
                        "KnotBook", "app", "version-info.json"));
                JSONObject object = new JSONObject(json);
                String build = object.getString("build");
                int year = Integer.parseInt(build.substring(0, 4));
                int month = Integer.parseInt(build.substring(4, 6));
                int day = Integer.parseInt(build.substring(6, 8));
                updateBuildVersion(year, month, day);
                imageVersion = object.getString("image");
            } catch (IOException ignored) {
            }
        }
    }

    private void updateBuildVersion(int year, int month, int day) {
        int minor;
        if (year == 2019) {
            minor = month - 8;
        } else {
            minor = 4 + month + (year - 2020) * 12;
        }
        buildVersion = "3." + minor + "." + day;
    }

    private void exit() {
        for (Service service : extensions.services) {
            service.terminate();
        }
        config.save();
        System.exit(0);
    }

    private TextEditor createTextEditor() {
        if (!textEditors.services.isEmpty()) {
            return textEditors.services.get(0).create();
        }
        throw new NoSuchElementException();
    }
}
