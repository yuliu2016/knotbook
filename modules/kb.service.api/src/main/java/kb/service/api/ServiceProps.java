package kb.service.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface ServiceProps {

    void put(@NotNull String key, @NotNull String value);

    default void put(@NotNull String key, boolean value) {
        put(key, String.valueOf(value));
    }

    default void put(@NotNull String key, int value) {
        put(key, String.valueOf(value));
    }

    @Nullable
    String get(@NotNull String key);

    @NotNull
    default String get(@NotNull String key, @NotNull String defVal) {
        String v = get(key);
        return v == null ? defVal : v;
    }

    default boolean getBoolean(@NotNull String key, boolean defVal) {
        String v = get(key);
        if (v == null) {
            return defVal;
        }
        String s = v.strip();
        return s.equalsIgnoreCase("true") || (!s.equalsIgnoreCase("false") && defVal);
    }

    default int getInt(@NotNull String key, int defVal) {
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

    void remove(@NotNull String key);

    boolean contains(@NotNull String key);

    void addListener(@NotNull String key, @NotNull ServicePropListener listener);

    void removeListener(@NotNull String key);
}
