package kb.tool.cng;

import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;

public class CNGService implements Service {

    private static ServiceMetadata metadata = new ServiceMetadata();

    static {
        metadata.setPackageName("kb.tool.cng");
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
