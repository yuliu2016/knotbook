package kb.service.api;


@SuppressWarnings("unused")
public interface Service extends MetaService {

    /**
     * Launch the service with a context
     */
    void launch(ServiceContext context);

    /**
     * @return Whether this service is available
     * (e.g. check all modules are loaded, check for external libs)
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * Stops the service
     */
    default void terminate() {
    }
}
