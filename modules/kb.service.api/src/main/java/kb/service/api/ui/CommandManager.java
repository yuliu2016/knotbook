package kb.service.api.ui;

import javafx.scene.input.KeyCombination;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.Ikon;

public interface CommandManager {
    void registerCommand(
            @NotNull String name,
            @Nullable Ikon icon,
            @Nullable KeyCombination shortcut,
            @NotNull CommandCallback callback
    );

    void registerHiddenCommand(
            @NotNull String name,
            @Nullable Ikon icon,
            @NotNull CommandCallback callback
    );
}
