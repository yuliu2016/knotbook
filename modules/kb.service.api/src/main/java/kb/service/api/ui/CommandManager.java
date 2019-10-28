package kb.service.api.ui;

import javafx.scene.input.KeyCombination;
import org.kordamp.ikonli.Ikon;

public interface CommandManager {
    void registerCommand(
            String name,
            Ikon icon,
            KeyCombination shortcut,
            CommandCallback callback
    );

    void registerHiddenCommand(
            String name,
            Ikon icon,
            CommandCallback callback
    );
}
