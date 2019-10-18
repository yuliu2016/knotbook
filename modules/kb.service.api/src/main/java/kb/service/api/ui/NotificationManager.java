package kb.service.api.ui;

import org.jetbrains.annotations.NotNull;

public interface NotificationManager {
    void setStatus(@NotNull String status);

    void pushPopup(@NotNull String title, @NotNull String message);

    void pushActionablePopup(
            @NotNull String title,
            @NotNull String message,
            @NotNull String action,
            @NotNull CommandCallback callback
    );

}
