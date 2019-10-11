package kb.core.application;

public interface RegistryHandle {
    String load();

    void save(String content);
}
