package kb.core.application;

import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;
import kb.service.api.application.ApplicationService;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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

    private static String getBuildVersion(String build) {
        int year = Integer.parseInt(build.substring(0, 4));
        int month = Integer.parseInt(build.substring(4, 6));
        int day = Integer.parseInt(build.substring(6, 8));
        return getBuildVersion(year, month, day);
    }

    private static ConfigHandle getHandle(boolean debug, String home) {
        return new FileConfigHandle(Paths.get(home, ".knotbook",
                debug ? "knotbook-config-debug.json" : "knotbook-config-release.json"));
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
        config = new Config(getHandle(isDebug(), home));
        manager = new ManagerImpl(this);
        if (!applications.services.isEmpty()) {
            app = applications.services.get(0);
            launchApplication();
        } else throw new IllegalStateException("No Application Found");
    }

    private void launchApplication() {
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

    String fetchUpdatedVersion() {
        String api = "https://dev.azure.com/yuliu2016/knotbook/_apis/build/builds?branchName=refs/heads/master&$top=1&api-version=5.1";
        String v;

        try {
            URL url = new URL(api);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "KnotBook");
            InputStream inputStream = conn.getInputStream();
            String result = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            JSONObject object = new JSONObject(result).getJSONArray("value").getJSONObject(0);
            String build = object.getString("buildNumber");
            v = getBuildVersion(build);
        } catch (Exception e) {
            e.printStackTrace();
            v = null;
        }
        String s = "The current version is " + getBuildVersion() + "\n";
        if (v != null) {
            if (v.equals(getBuildVersion())) {
                s += "This version is up-to-date";
            } else {
                s += "The latest version available: " + v;
            }
        } else {
            s += "Cannot Check for Update";
        }
        return s;
    }

    private void updateVersion() {
        if (isDebug()) {
            updateDebugVersion();
        } else {
            try {
                String json = Files.readString(Paths.get(getKnotBook().home, ".knotbook",
                        "KnotBook", "app", "version-info.json"));
                JSONObject object = new JSONObject(json);
                String build = object.getString("build");
                buildVersion = getBuildVersion(build);
                imageVersion = object.getString("image");
            } catch (IOException ignored) {
                updateDebugVersion();
            }
        }
    }

    private void updateDebugVersion() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        buildVersion = getBuildVersion(year, month, day) + "-dev";
        imageVersion = "None";
    }

    void exit() {
        for (Service service : extensions.services) {
            service.terminate();
        }
        config.save();
        System.exit(0);
    }
}
