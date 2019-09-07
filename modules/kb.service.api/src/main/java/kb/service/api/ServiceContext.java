package kb.service.api;

import org.jetbrains.annotations.NotNull;

public interface ServiceContext {
    /**
     * Get the service that initiated this ServiceContext
     */
    @NotNull
    Service getService();

    /**
     * Get the properties that belongs to this context
     */
    @NotNull
    ServiceProps getProps();
}
