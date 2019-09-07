package kb.core.context;

import kb.service.api.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public class ApplicationContext {

    private static final List<Service> services = new ArrayList<>();

    public static void launch(Runnable runnable) {
        Registry.INSTANCE.load();
        ServiceLoader<Service> loader = ServiceLoader.load(Service.class);
        for (Service service : loader) {
            services.add(service);
        }
        if (runnable != null) {
            runnable.run();
        }
    }
}
