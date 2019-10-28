package kb.service.api;


@SuppressWarnings("unused")
public interface ServiceProps {

    void put(String key, String value);

    default void put(String key, boolean value) {
        put(key, String.valueOf(value));
    }

    default void put(String key, int value) {
        put(key, String.valueOf(value));
    }

    String get(String key);


    default String get(String key, String defVal) {
        String v = get(key);
        return v == null ? defVal : v;
    }

    default boolean getBoolean(String key, boolean defVal) {
        String v = get(key);
        if (v == null) {
            return defVal;
        }
        String s = v.strip();
        return s.equalsIgnoreCase("true") || (!s.equalsIgnoreCase("false") && defVal);
    }

    default int getInt(String key, int defVal) {
        String v = get(key);
        if (v == null) {
            return defVal;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    void remove(String key);

    boolean contains(String key);

    void addListener(String key, ServicePropListener listener);

    void removeListener(String key);
}
