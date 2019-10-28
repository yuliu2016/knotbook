package kb.core.camera.fx;

import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;

public class FXCameraService implements Service {

    private static ServiceMetadata metadata = new ServiceMetadata();

    static {
        metadata.setPackageName("kb.core.camera.fx");
        metadata.setPackageVersion("1.0");
    }

    @Override
    public void launch(ServiceContext context) {

    }

    @Override
    public ServiceMetadata getMetadata() {
        return metadata;
    }
}
