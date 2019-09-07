package kb.service.api;

@SuppressWarnings("unused")
public interface Service {

    ServiceMetadata getMetadata();

    /**
     * @return Whether this service is availabe
     */
    boolean isAvailable();

    /**
     * Launch the service with a context
     */
    void launch(ServiceContext context);
}
