package kb.service.api.optionbar;

import javafx.scene.input.KeyCombination;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.Ikon;

import java.util.function.Consumer;

public interface CommandManager {
    void registerCommand(
            @NotNull String name,
            @Nullable Ikon icon,
            @Nullable KeyCombination shortcut,
            @NotNull Consumer<OptionBar> callback
    );

    void registerHiddenCommand(
            @NotNull String name,
            @Nullable Ikon icon,
            @NotNull Consumer<OptionBar> callback
    );
}
