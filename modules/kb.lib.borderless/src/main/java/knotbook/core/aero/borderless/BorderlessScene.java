package knotbook.core.aero.borderless;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Undecorated JavaFX Scene with implemented move, resize, minimise, maximise and Windows A e r o Snap controls.
 * <p>
 * Usage:
 * <pre>
 * {@code
 * // Constructor using your primary stage and the root Parent of your content.
 * BorderlessScene scene = new BorderlessScene(yourPrimaryStage, yourParent);
 * yourPrimaryStage.setScene(scene); // Set the scene to your stage and you're done!
 *
 * // Maximise (on/off) and minimise the application:
 * scene.maximise();
 * scene.minimise();
 *
 * // To move the window around by pressing a node:
 * scene.setMoveControl(yourNode);
 *
 * // To disable resize:
 * scene.setResizable(false);
 *
 * // To switch the content during runtime:
 * scene.setContent(yourNewParent);
 *
 * // Check if maximised:
 * Boolean bool = scene.isMaximised();
 *
 * // Get windowed size and position:
 * scene.getWindowedSize();
 * scene.getWindowedPosition();
 * }
 * </pre>
 *
 * @author Nicolas Senet-Larson
 * @version 1.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class BorderlessScene extends Scene {

    private BorderlessController controller;
    private AnchorPane rootPane;
    private Stage primaryStage;

    /**
     * The constructor.
     *
     * @param primaryStage your stage.
     * @param content      the root Parent of your content.
     */
    public BorderlessScene(Stage primaryStage, Parent content) {
        super(new Pane());

        this.rootPane = BorderlessFXML.pane;
        setRoot(this.rootPane);
        setContent(content);

        this.controller = new BorderlessController();
        this.controller.setMainApp(primaryStage);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        this.primaryStage = primaryStage;
    }

    /**
     * Change the content of the scene.
     *
     * @param content the root Parent of your new content.
     */
    public void setContent(Parent content) {
        this.rootPane.getChildren().remove(0);
        this.rootPane.getChildren().add(0, content);
        AnchorPane.setLeftAnchor(content, 0.0D);
        AnchorPane.setTopAnchor(content, 0.0D);
        AnchorPane.setRightAnchor(content, 0.0D);
        AnchorPane.setBottomAnchor(content, 0.0D);
    }

    /**
     * Set a node that can be pressed and dragged to move the application around.
     *
     * @param node the node.
     */
    public void setMoveControl(Node node) {
        this.controller.setMoveControl(node);
    }

    /**
     * Toggle to maximise the application.
     */
    public void maximise() {
        controller.maximise();
    }

    /**
     * Minimise the application to the task bar.
     */
    public void minimise() {
        controller.minimise();
    }

    /**
     * Disable/enable the resizing of your application. Enabled by default.
     *
     * @param bool false to disable, true to enable.
     */
    public void setResizable(Boolean bool) {
        controller.setResizable(bool);
    }

    /**
     * Check the maximised state of the application.
     *
     * @return true if the window is maximised.
     */
    public Boolean isMaximised() {
        return controller.maximised;
    }

    /**
     * Returns the width and height of the application when windowed.
     *
     * @return instance of Delta class. Delta.x = width, Delta.y = height.
     */
    public Delta getWindowedSize() {
        if (controller.prevSize.x == null)
            controller.prevSize.x = primaryStage.getWidth();
        if (controller.prevSize.y == null)
            controller.prevSize.y = primaryStage.getHeight();
        return controller.prevSize;
    }

    /**
     * Returns the x and y position of the application when windowed.
     *
     * @return instance of Delta class. Use Delta.x and Delta.y.
     */
    public Delta getWindowedPosition() {
        if (controller.prevPos.x == null)
            controller.prevPos.x = primaryStage.getX();
        if (controller.prevPos.y == null)
            controller.prevPos.y = primaryStage.getY();
        return controller.prevPos;
    }
}
