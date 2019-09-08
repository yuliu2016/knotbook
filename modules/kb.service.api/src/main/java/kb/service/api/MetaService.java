package kb.service.api;

import org.jetbrains.annotations.NotNull;

public interface MetaService {

    /**
     * Get the metadata for this service
     */
    @NotNull
    ServiceMetadata getMetadata();
}
