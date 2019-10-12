package kb.service.api;

@FunctionalInterface
public interface ServicePropListener {
    void propertyChanged(String oldVal, String newVal);
}
