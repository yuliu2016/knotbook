package kb.service.api.ui;

public interface UIManager {

    boolean isOptionBarShown();

    void showOptionBar(OptionBar optionBar);

    void registerCommand(String id, Command command);

    boolean hasCommand(String id);

    void invokeCommand(String id);

    /**
     * Create a notification handler
     */
    Notification createNotification();
}
