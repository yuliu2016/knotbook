package kb.service.api;

public interface ServiceProps {
    void put(String key, boolean value);

    void put(String key, int value);

    void put(String key, String value);

    String get(String key);

    String get(String key, String defVal);

    boolean getBoolean(String key, boolean defVal);

    int getInt(String key, int defVal);

    void remove(String key);

    boolean contains(String key);

    void commit();

    void addListener(String key, ServicePropListener listener);
}
