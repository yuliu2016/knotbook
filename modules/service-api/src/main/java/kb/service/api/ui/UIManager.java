package kb.service.api.ui;

import javafx.scene.input.KeyCombination;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface UIManager {

    void showOptionBar(OptionBar optionBar);

    void registerCommand(String id, Command command);

    boolean hasCommand(String id);

    void invokeCommand(String id);

    void showAlert(String title, String message);

    void confirmOK(String title, String message, Runnable runIfOk);

    void confirmYes(String title, String message, Runnable runIfYes);

    void showException(Throwable e);

    void getTextInput(String prompt, Consumer<String> callback);

    TextEditor createTextEditor();

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
