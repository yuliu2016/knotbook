package kb.service.api.ui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

@SuppressWarnings({"unused", "WeakerAccess"})
public class OptionBar {

    public static class Item {
        private String type;
        private String name;
        private String info;
        private Node graphic;

        public Item(String type, String name, String info, Node graphic) {
            this.type = type;
            this.name = name;
            this.info = info;
            this.graphic = graphic;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getInfo() {
            return info;
        }

        public Node getGraphic() {
            return graphic;
        }
    }

    private StringProperty hintProperty = new SimpleStringProperty(null);

    public StringProperty getHintProperty() {
        return hintProperty;
    }

    public String getHint() {
        return getHintProperty().get();
    }

    public void setHint(String hint) {
        getHintProperty().set(hint);
    }

    private ObjectProperty<Node> arbitraryViewProperty = new SimpleObjectProperty<>(null);

    public ObjectProperty<Node> getArbitraryViewProperty() {
        return arbitraryViewProperty;
    }

    public Node getArbitraryView() {
        return getArbitraryViewProperty().get();
    }

    public void setArbitraryView(Node arbitraryView) {
        getArbitraryViewProperty().set(arbitraryView);
    }

    private ObservableList<Item> itemsProperty = FXCollections.observableArrayList();

    public ObservableList<Item> getItems() {
        return itemsProperty;
    }

    private ObjectProperty<EventHandler<ActionEvent>> onDismissedProperty = new SimpleObjectProperty<>();

    public ObjectProperty<EventHandler<ActionEvent>> getOnDismissedProperty() {
        return onDismissedProperty;
    }

    public EventHandler<ActionEvent> getOnDismissed() {
        return getOnDismissedProperty().get();
    }

    public void setOnDismissed(EventHandler<ActionEvent> onDismissed) {
        getOnDismissedProperty().set(onDismissed);
    }

    private StringProperty textProperty = new SimpleStringProperty();

    public StringProperty getTextProperty() {
        return textProperty;
    }

    public String getText() {
        return getTextProperty().get();
    }

    public void setText(String text) {
        getTextProperty().set(text);
    }

    private IntegerProperty selectedItemProperty = new SimpleIntegerProperty();

    public IntegerProperty getSelectedItemProperty() {
        return selectedItemProperty;
    }

    public int getSelectedItem() {
        return getSelectedItemProperty().get();
    }

    public void setSelectedItem(int selectedItem) {
        getSelectedItemProperty().set(selectedItem);
    }
}
