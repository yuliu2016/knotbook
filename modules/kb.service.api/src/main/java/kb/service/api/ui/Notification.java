package kb.service.api.ui;


public interface Notification {
    Notification setInfo();

    Notification setWarning();

    Notification setError();

    Notification setMessage(String message);

    Notification addAction(String name, CommandCallback callback);

    Notification show();
}
