package kb.service.api.ui;

import org.jetbrains.annotations.NotNull;

public interface Notification {
    Notification setInfo();

    Notification setWarning();

    Notification setError();

    Notification setMessage(@NotNull String message);

    Notification addAction(@NotNull String name, @NotNull CommandCallback callback);

    Notification show();
}
