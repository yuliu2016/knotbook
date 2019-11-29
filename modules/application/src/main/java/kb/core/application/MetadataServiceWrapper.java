package kb.core.application;

import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;

class MetadataServiceWrapper implements Service {
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
