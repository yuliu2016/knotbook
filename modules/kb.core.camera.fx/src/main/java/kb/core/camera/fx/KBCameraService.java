package kb.core.camera.fx;

import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;
import org.jetbrains.annotations.NotNull;

public class KBCameraService implements Service {

    private static ServiceMetadata metadata = new ServiceMetadata();

    static {
        metadata.setPackageName("kb.core.camera.fx");
        metadata.setPackageVersion("1.0");
    }

    @Override
    public void launch(@NotNull ServiceContext context) {

    }

    @NotNull
    @Override
    public ServiceMetadata getMetadata() {
        return metadata;
    }
}
