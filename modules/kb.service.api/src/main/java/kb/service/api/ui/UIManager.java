package kb.service.api.ui;

import javafx.scene.input.KeyCombination;
import org.kordamp.ikonli.Ikon;

public interface UIManager {

    boolean isOptionBarShown();

    boolean showOptionBar(OptionBar optionBar);

    void registerCommand(
            String commandID,
            String commandType,
            String commandName,
            Ikon icon,
            KeyCombination shortcut,
            Runnable callback
    );

    boolean hasCommand(String commandID);

    void invokeCommand(String commandID);
}
