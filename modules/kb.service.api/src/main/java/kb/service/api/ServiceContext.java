package kb.service.api;

public interface ServiceContext {
    /**
     * Get the service that initiated this ServiceContext
     */
    Service getService();

    /**
     * Get the properties that belongs to this context
     */
    ServiceProps getProps();
}
