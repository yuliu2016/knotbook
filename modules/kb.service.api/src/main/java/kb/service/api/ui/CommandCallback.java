package kb.service.api.ui;

@FunctionalInterface
public interface CommandCallback {
    void onCommandInvoked(OptionBar optionBar);
}
