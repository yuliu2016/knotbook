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

    private StringProperty hintProperty = new SimpleStringProperty(null);

    public StringProperty hintProperty() {
        return hintProperty;
    }

    public String getHint() {
        return hintProperty().get();
    }

    public void setHint(String hint) {
        hintProperty().set(hint);
    }

    private ObjectProperty<Node> arbitraryViewProperty = new SimpleObjectProperty<>(null);

    public ObjectProperty<Node> arbitraryViewProperty() {
        return arbitraryViewProperty;
    }

    public Node getArbitraryView() {
        return arbitraryViewProperty().get();
    }

    public void setArbitraryView(Node arbitraryView) {
        arbitraryViewProperty().set(arbitraryView);
    }

    private ObservableList<OptionBarItem> itemsProperty = FXCollections.observableArrayList();

    public ObservableList<OptionBarItem> getItems() {
        return itemsProperty;
    }

    private ObjectProperty<EventHandler<ActionEvent>> onDismissedProperty = new SimpleObjectProperty<>();

    public ObjectProperty<EventHandler<ActionEvent>> onDismissedProperty() {
        return onDismissedProperty;
    }

    public EventHandler<ActionEvent> getOnDismissed() {
        return onDismissedProperty().get();
    }

    public void setOnDismissed(EventHandler<ActionEvent> onDismissed) {
        onDismissedProperty().set(onDismissed);
    }

    private ObjectProperty<EventHandler<ActionEvent>> onContinueProperty = new SimpleObjectProperty<>();

    public ObjectProperty<EventHandler<ActionEvent>> onContinueProperty() {
        return onContinueProperty;
    }

    public EventHandler<ActionEvent> getOnContinue() {
        return onContinueProperty().get();
    }

    public void setOnContinue(EventHandler<ActionEvent> onContinue) {
        onContinueProperty().set(onContinue);
    }

    private ObjectProperty<EventHandler<ActionEvent>> onEnterPressedProperty = new SimpleObjectProperty<>();

    public ObjectProperty<EventHandler<ActionEvent>> onEnterPressedProperty() {
        return onEnterPressedProperty;
    }

    public EventHandler<ActionEvent> getOnEnterPressed() {
        return onEnterPressedProperty().get();
    }

    public void setOnEnterPressed(EventHandler<ActionEvent> onEnterPressed) {
        onEnterPressedProperty().set(onEnterPressed);
    }

    private StringProperty textProperty = new SimpleStringProperty();

    public StringProperty textProperty() {
        return textProperty;
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        textProperty().set(text);
    }

    private IntegerProperty selectedItemProperty = new SimpleIntegerProperty();

    public IntegerProperty selectedItemProperty() {
        return selectedItemProperty;
    }

    public int getSelectedItem() {
        return selectedItemProperty().get();
    }

    public void setSelectedItem(int selectedItem) {
        selectedItemProperty().set(selectedItem);
    }

    private StringProperty highlightedProperty = new SimpleStringProperty();

    public StringProperty highlightedProperty() {
        return highlightedProperty;
    }

    public String getHighlighted() {
        return highlightedProperty.get();
    }

    public void setHighlighted(String highlighted) {
        highlightedProperty.set(highlighted);
    }
}
