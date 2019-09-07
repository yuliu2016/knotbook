package kb.service.api;

@FunctionalInterface
public interface ServicePropListener {
    void propertyChanged(String key, String oldVal, String newVal);
}
