package kb.service.api;

import org.jetbrains.annotations.NotNull;

public interface ServiceProps {
    void put(@NotNull String key, boolean value);

    void put(@NotNull String key, int value);

    void put(@NotNull String key, @NotNull String value);

    String get(@NotNull String key);

    String get(@NotNull String key, @NotNull String defVal);

    boolean getBoolean(@NotNull String key, boolean defVal);

    int getInt(@NotNull String key, int defVal);

    void remove(@NotNull String key);

    boolean contains(@NotNull String key);

    void commit();

    void addListener(@NotNull String key, @NotNull ServicePropListener listener);
}
