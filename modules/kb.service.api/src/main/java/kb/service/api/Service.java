package kb.service.api;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Service extends MetaService {

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

    /**
     * @return true if the service cannot be paused or stopped now
     */
    default boolean isBusy() {
        return false;
    }

    /**
     * Stops the service
     *
     * @return true if service stopped successfully
     */
    default boolean terminate() {
        return true;
    }

    /**
     * Stops the service forcefully
     */
    default void forceTerminate() {

    }
}
