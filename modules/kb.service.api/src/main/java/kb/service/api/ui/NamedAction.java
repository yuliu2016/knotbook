package kb.service.api.ui;

public class NamedAction {
    private String name;

    private CommandCallback callback;

    public NamedAction(String name, CommandCallback callback) {
        this.name = name;
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public CommandCallback getCallback() {
        return callback;
    }
}
