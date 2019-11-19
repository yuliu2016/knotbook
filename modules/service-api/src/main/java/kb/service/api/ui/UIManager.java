package kb.service.api.ui;

import javafx.scene.input.KeyCombination;

@SuppressWarnings("unused")
public interface UIManager {

    boolean isOptionBarShown();

    void showOptionBar(OptionBar optionBar);

    void registerCommand(String id, Command command);

    boolean hasCommand(String id);

    void invokeCommand(String id);

    void showAlert(String title, String message);

    default void registerCommand(
            String id,
            String name,
            String icon,
            KeyCombination shortcut,
            Runnable callback
    ) {
        registerCommand(id, new Command(name, icon, shortcut, callback));
    }

    default void registerCommand(
            String id,
            String name,
            String icon,
            Runnable callback
    ) {
        registerCommand(id, new Command(name, icon, null, callback));
    }
}
