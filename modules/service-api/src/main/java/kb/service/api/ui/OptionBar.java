package kb.service.api.ui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

/**
 * Set options for the option bar.
 * Properties are observable`
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class OptionBar {

    private BooleanProperty showing = new SimpleBooleanProperty(false);

    public BooleanProperty showingProperty() {
        return showing;
    }

    public boolean isShowing() {
        return showingProperty().get();
    }

    public void setShowing(boolean showing) {
        showingProperty().set(showing);
    }

    private StringProperty hint = new SimpleStringProperty(null);

    public StringProperty hintProperty() {
        return hint;
    }

    public String getHint() {
        return hintProperty().get();
    }

    public void setHint(String hint) {
        hintProperty().set(hint);
    }

    private ObjectProperty<Node> arbitraryView = new SimpleObjectProperty<>(null);

    public ObjectProperty<Node> arbitraryViewProperty() {
        return arbitraryView;
    }

    public Node getArbitraryView() {
        return arbitraryViewProperty().get();
    }

    public void setArbitraryView(Node arbitraryView) {
        arbitraryViewProperty().set(arbitraryView);
    }

    private ObservableList<OptionItem> items = FXCollections.observableArrayList();

    public ObservableList<OptionItem> getItems() {
        return items;
    }

    private ObjectProperty<EventHandler<ActionEvent>> onDismissed = new SimpleObjectProperty<>();

    public ObjectProperty<EventHandler<ActionEvent>> onDismissedProperty() {
        return onDismissed;
    }

    public EventHandler<ActionEvent> getOnDismissed() {
        return onDismissedProperty().get();
    }

    public void setOnDismissed(EventHandler<ActionEvent> onDismissed) {
        onDismissedProperty().set(onDismissed);
    }

    private ObjectProperty<EventHandler<ActionEvent>> onHideAndContinue = new SimpleObjectProperty<>(null);

    public ObjectProperty<EventHandler<ActionEvent>> onHideAndContinueProperty() {
        return onHideAndContinue;
    }

    public EventHandler<ActionEvent> getOnHideAndContinue() {
        return onHideAndContinueProperty().get();
    }

    public void setOnHideAndContinue(EventHandler<ActionEvent> onHideAndContinue) {
        onHideAndContinueProperty().set(onHideAndContinue);
    }

    private ObjectProperty<EventHandler<ActionEvent>> onEnterPressed = new SimpleObjectProperty<>(null);

    public ObjectProperty<EventHandler<ActionEvent>> onEnterPressedProperty() {
        return onEnterPressed;
    }

    public EventHandler<ActionEvent> getOnEnterPressed() {
        return onEnterPressedProperty().get();
    }

    public void setOnEnterPressed(EventHandler<ActionEvent> onEnterPressed) {
        onEnterPressedProperty().set(onEnterPressed);
    }

    private StringProperty text = new SimpleStringProperty();

    public StringProperty textProperty() {
        return text;
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        textProperty().set(text);
    }

    private IntegerProperty selectedItem = new SimpleIntegerProperty();

    public IntegerProperty selectedItemProperty() {
        return selectedItem;
    }

    public int getSelectedItem() {
        return selectedItemProperty().get();
    }
}
