package kb.service.api.ui;

import javafx.scene.Node;

public class OptionBarItem {
    private String name;
    private String info;
    private Node graphic;

    /**
     * Creates a new item
     *
     * @param name    the name of the item
     * @param info    additional info
     * @param graphic a graphic item, like an icon or a checkbox
     */
    public OptionBarItem(String name, String info, Node graphic) {
        this.name = name;
        this.info = info;
        this.graphic = graphic;
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
