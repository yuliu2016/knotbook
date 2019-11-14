package kb.service.api.ui;

import javafx.scene.input.KeyCombination;

/**
 * Represents a command of the UI
 */
public class Command {
    private String name;
    private String icon;
    private KeyCombination shortcut;
    private Runnable callback;

    public Command(String name, String icon, KeyCombination shortcut, Runnable callback) {
        this.name = name;
        this.icon = icon;
        this.shortcut = shortcut;
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public KeyCombination getShortcut() {
        return shortcut;
    }

    public Runnable getCallback() {
        return callback;
    }
}
