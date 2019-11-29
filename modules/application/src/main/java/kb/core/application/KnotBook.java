package kb.core.application;

import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;
import kb.service.api.application.ApplicationService;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unused")
class KnotBook {

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

    private static String getBuildVersion(int year, int month, int day) {
        int minor;
        if (year == 2019) {
            minor = month - 8;
        } else {
            minor = 4 + month + (year - 2020) * 12;
        }
        return "3." + minor + "." + day;
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

    private Config config;
    private ManagerImpl manager;
    private ApplicationService app;
    private String buildVersion;
    private String imageVersion;

    void launch(List<String> args) {
        this.args = args;
        config = new Config(getHandle());
        manager = new ManagerImpl(this);
        if (!applications.services.isEmpty()) {
            app = applications.services.get(0);
            launchApplication();
        } else throw new IllegalStateException("No Application Found");
    }

    private void launchApplication() {
        extensions.print();
        ServiceContext context = contextForService(serviceForApplication(app.getMetadata()), app);
        app.launch(manager, context, this::launchServices);
    }

    private void launchServices() {
        for (Service service : extensions.services) {
            launchService(service, app);
        }
    }

    Config getConfig() {
        return config;
    }

    List<String> getJVMArgs() {
        return args;
    }

    List<Service> getServices() {
        return extensions.services;
    }

    String getBuildVersion() {
        if (buildVersion == null) {
            updateVersion();
        }
        return buildVersion;
    }

    String getImageVersion() {
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
            buildVersion = getBuildVersion(year, month, day) + "-dev";
        } else {
            try {
                String json = Files.readString(Paths.get(getKnotBook().home, ".knotbook",
                        "KnotBook", "app", "version-info.json"));
                JSONObject object = new JSONObject(json);
                String build = object.getString("build");
                int year = Integer.parseInt(build.substring(0, 4));
                int month = Integer.parseInt(build.substring(4, 6));
                int day = Integer.parseInt(build.substring(6, 8));
                buildVersion = getBuildVersion(year, month, day);
                imageVersion = object.getString("image");
            } catch (IOException ignored) {
            }
        }
    }

    void exit() {
        for (Service service : extensions.services) {
            service.terminate();
        }
        config.save();
        System.exit(0);
    }
}
