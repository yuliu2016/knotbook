package kb.service.api;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Service {

    /**
     * Get the metadata for this service
     */
    @NotNull
    ServiceMetadata getMetadata();

    /**
     * Launch the service with a context
     */
    void launch(@NotNull ServiceContext context);

    /**
     * @return Whether this service is availabe
     * (e.g. check all modules are loaded, check for external libs)
     */
    default boolean isAvailable() {
        return true;
    }
}
